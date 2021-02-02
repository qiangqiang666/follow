package com.monkey.follow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.enums.*;
import com.monkey.follow.mapper.ContentMapper;
import com.monkey.follow.mapper.FirmOfferHisMapper;
import com.monkey.follow.mapper.SysConfigMapper;
import com.monkey.follow.model.ContentEntity;
import com.monkey.follow.model.FirmOfferHis;
import com.monkey.follow.model.jsoup.AccountInfo;
import com.monkey.follow.model.jsoup.Position;
import com.monkey.follow.okex.futures.param.ClosePositions;
import com.monkey.follow.okex.futures.param.Order;
import com.monkey.follow.okex.futures.result.OrderResult;
import com.monkey.follow.okex.service.account.AccountAPIService;
import com.monkey.follow.okex.service.futures.FuturesTradeAPIService;
import com.monkey.follow.service.CoinService;
import com.monkey.follow.utils.DingDingRobotUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 描述:
 * 〈币coin跟单〉
 *
 * @author Monkey
 * @create 2020/5/6 15:08
 */
@Slf4j
@Service
public class CoinServiceImpl implements CoinService {
    @Resource
    private FirmOfferHisMapper firmOfferHisMapper;
    @Autowired
    private FuturesTradeAPIService tradeAPIService;
    @Autowired
    private FuturesTradeAPIService futuresTradeAPIService;
    @Autowired
    private AccountAPIService accountAPIService;
    @Resource
    private SysConfigMapper sysConfigMapper;
    @Resource
    private ContentMapper contentMapper;

    @Override
    public void getLatestOperation(String typeStr) {
        // 获取币coin跟单用户的操作记录
        String result = getCoinData(typeStr);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            List<FirmOfferHis> firmOfferHisList = jsonObject.getJSONObject("data").getJSONArray("firmOfferHisList").toJavaList(FirmOfferHis.class);

            for (FirmOfferHis firmOfferHis : firmOfferHisList) {
                // 查询是否存在db,不存在则进行下单
                FirmOfferHis selectResult = firmOfferHisMapper.selectByMsgId(firmOfferHis.getMsgId());
                if (null == selectResult) {
                    if (SysConfInterface.isFollow) {
                        log.info("跟单...............");
                        /**
                         * 下单
                         */
                        Order order = new Order();
                        order.setClient_oid(RandomStringUtils.randomAlphabetic(32));
                        order.setOrder_type(OrderTypeEnum.MARKET.code());
                        // 获取张数
                        order.setSize(getSize(firmOfferHis.getContent(), firmOfferHis.getSym(), SysConfInterface.contractClauses));
                        // 获取操作类型
                        String type = getType(firmOfferHis.getLabelSub());
                        if (StringUtils.isNotEmpty(type)) {
                            order.setType(type);
                        } else {
                            continue;
                        }
                        // 获取合约id
                        String instrument_id = getInstrumentId(firmOfferHis.getSym(), SysConfInterface.contractClauses);
                        if (StringUtils.isNotEmpty(instrument_id)) {
                            order.setinstrument_id(instrument_id);
                        } else {
                            // 不再预定中的合约信息,直接插入数据库,算失败处理,避免重复判断
                            firmOfferHis.setFollowStatus(1);
                            firmOfferHisMapper.insertSelective(firmOfferHis);
                            log.info("未在预定的合约中,插入数据,跟单取消........");
                            continue;
                        }
                        try {
                            // 下单
                            insert(order, firmOfferHis);
                        } catch (Exception e1) {
                            if (e1.getMessage().equalsIgnoreCase(OkexCodeEnum.code_32014.msg())) {
                                try {
                                    //市价全平
                                    closeOut(firmOfferHis.getLabelSub(), instrument_id);
                                } catch (Exception e11) {
                                    firmOfferHis.setFollowStatus(1);
                                    firmOfferHisMapper.insertSelective(firmOfferHis);
                                    log.error("全平失败,参数为:" + firmOfferHis + "失败原因:{}", e11);
                                }
                            } else {
                                log.error("下单失败,进行重试,参数为:" + firmOfferHis + "失败原因:{}", e1);
                                try {
                                    // 下单
                                    insert(order, firmOfferHis);
                                } catch (Exception e2) {
                                    // 下单失败也插入记录,避免重复下单
                                    firmOfferHis.setFollowStatus(1);
                                    firmOfferHisMapper.insertSelective(firmOfferHis);
                                    log.error("重试下单失败,结束当前下单,参数为:" + firmOfferHis + "失败原因:{}", e2);
                                }
                            }

                        }
                    } else {
                        log.info("未跟单...............");
                        firmOfferHis.setFollowStatus(1);
                        firmOfferHisMapper.insertSelective(firmOfferHis);
                    }
                }
            }
        } else {
            log.warn("查询币coin最新操作数据,未出现数据..........");
        }
    }

    /**
     * 止损
     * ps:
     * 1.okex请求返回200,却balance为0,实际上账户是有钱的,
     * 2.okex请求返回200,但是返回结果却没有balance字段
     * 所以为了减少误止损,需要设置止损触发次数,来判定
     *
     * @param accountType 账户类型
     * @param currency    币本位
     */
    @Override
    public void stopLoss(String accountType, String currency) {
        try {
            if (SysConfInterface.isStopLoss) {
                JSONObject usd = accountAPIService.getAllAccount(accountType, currency);
                Integer account_type = usd.getInteger("account_type");
                BigDecimal balance = usd.getBigDecimal("balance");
                // 如果小于设定的止损数值,则进行全仓位市价平仓
                if (account_type == 3 && balance.compareTo(SysConfInterface.stopLossNum) <= 0) {
                    // 增加止损触发数
                    sysConfigMapper.updateNum(1);
                    // 触发止损次数,则进行止损
                    if (sysConfigMapper.selectByPrimaryKey(1).getStopLossTimes() >= 3) {
                        /**
                         * 市价全平
                         */
                        try {
                            // 平多BTC
                            closeOut(BizTypeEnum.sell_more.msg(), ContractEnum.BTC__USDT_QUARTER.code() + SysConfInterface.contractClauses);
                        } catch (Exception e) {
                            log.error("止损方法出现异常:", e);
                        }
                        try {
                            // 平空BTC
                            closeOut(BizTypeEnum.buy_empty.msg(), ContractEnum.BTC__USDT_QUARTER.code() + SysConfInterface.contractClauses);
                        } catch (Exception e) {
                            log.error("止损方法出现异常:", e);
                        }
                        try {
                            // 平多BTC
                            closeOut(BizTypeEnum.sell_more.msg(), ContractEnum.BTC_QUARTER.code() + SysConfInterface.contractClauses);
                        } catch (Exception e) {
                            log.error("止损方法出现异常:", e);
                        }
                        try {
                            // 平空BTC
                            closeOut(BizTypeEnum.buy_empty.msg(), ContractEnum.BTC_QUARTER.code() + SysConfInterface.contractClauses);
                        } catch (Exception e) {
                            log.error("止损方法出现异常:", e);
                        }
                        try {
                            // 平多ETH
                            closeOut(BizTypeEnum.sell_more.msg(), ContractEnum.ETH__USDT_QUARTER.code() + SysConfInterface.contractClauses);
                        } catch (Exception e) {
                            log.error("止损方法出现异常:", e);
                        }
                        try {
                            // 平空ETH
                            closeOut(BizTypeEnum.buy_empty.msg(), ContractEnum.ETH__USDT_QUARTER.code() + SysConfInterface.contractClauses);
                        } catch (Exception e) {
                            log.error("止损方法出现异常:", e);
                        }
//                        SysConfig sysConfig1 = sysConfigMapper.selectByType(ConfigTypeEnum.pro.code());
//                        SysConfig sysConfig2 = sysConfigMapper.selectByType(ConfigTypeEnum.test.code());
//                        sysConfig1.setStopLossTimes(0);
//                        sysConfig1.setIsFollow(false);
//                        sysConfig2.setIsFollow(false);
//                        sysConfigMapper.updateByPrimaryKeySelective(sysConfig1);
//                        sysConfigMapper.updateByPrimaryKeySelective(sysConfig2);
                        SysConfInterface.isFollow = false;
                    }
                }
            }
        } catch (Exception e) {
            if (e.getMessage().equalsIgnoreCase(OkexCodeEnum.code_30014.msg())) {
                log.warn("查询余额,频繁请求.......");
            } else {
                log.error("查询余额,出现异常,原因:{}.......", e);
            }
        }
    }

    @Override
    public void legacy(String typeStr) {
        try {
            String url = SysConfInterface.hostAddress + "/firmOffer/positions";
            Connection connection = Jsoup.connect(url);
            connection.ignoreContentType(true).ignoreHttpErrors(true).timeout(10000);
            connection.header("token", SysConfInterface.token);
            Document document = connection.get();
            String result = document.getElementsByTag("body").text();
        } catch (Exception e) {
            log.error("查询币coin最新操作数据,出现异常,异常原因{}", e);
        }
    }


    /**
     * 市价全平
     *
     * @param labelSub
     * @param InstrumentId
     * @return
     */
    private void closeOut(String labelSub, String InstrumentId) {
        ClosePositions closePositions = new ClosePositions();
        closePositions.setInstrument_id(InstrumentId);
        // 平空
        if (labelSub.equals(BizTypeEnum.buy_empty.msg())) {
            closePositions.setDirection(AllFlatTypeEnum.FLAT_NULL.code());
        }
        // 平多
        if (labelSub.equals(BizTypeEnum.sell_more.msg())) {
            closePositions.setDirection(AllFlatTypeEnum.FLAT_MORE.code());
        }
        futuresTradeAPIService.closePositions(closePositions);
    }

    /**
     * 获取币coin跟单的操作数据
     *
     * @param typeStr 跟单用户
     * @return
     */
    private String getCoinData(String typeStr) {
        try {
            String url = SysConfInterface.hostAddress + SysConfInterface.latestUrl + typeStr;
            Connection connection = Jsoup.connect(url);
            connection.ignoreContentType(true).ignoreHttpErrors(true).timeout(10000);
            connection.header("token", SysConfInterface.token);
            Document document = connection.get();
            String result = document.getElementsByTag("body").text();
            return result;
        } catch (Exception e) {
            log.error("查询币coin最新操作数据,出现异常,异常原因{}", e);
            return null;
        }
    }

    /**
     * 获取开单张数
     *
     * @param content 操作内容
     * @return
     */
    private String getSize(String content, String sym, String contractClauses) {
        String size;
        if (StringUtils.isEmpty(content)) {
            return SysConfInterface.minnNum;
        }
        int number = SysConfInterface.multipleNum;
        if (sym.equals(ContractEnum.BTC__USD_QUARTER.msg() + contractClauses)) {
            number = SysConfInterface.multipleNum / 3;
        }
        try {
//            String substring0 = content.substring(content.indexOf("挂单【") + 3);
//            String substring1 = substring0.substring(0, substring0.indexOf("，"));
//            String last = substring1.substring(0, substring1.indexOf("张】"));
            String last = content.substring(content.lastIndexOf("成交【") + 3, content.lastIndexOf("张】"));
            number = Integer.parseInt(last);
        } catch (Exception e) {
            log.warn("字符串截取出现异常,参数为:{}" + content);
        }
        int i = number / SysConfInterface.multipleNum;
        if (i <= 0) {
            size = SysConfInterface.minnNum;
        } else {
            size = String.valueOf(i);
        }
        return size;
    }

    /**
     * 获取操作类型
     *
     * @param labelSub 操作描述
     * @return
     */
    private String getType(String labelSub) {
        if (StringUtils.isEmpty(labelSub)) {
            return null;
        }
        String type;
        if (labelSub.equals(BizTypeEnum.sell_empty.msg())) {
            type = BizTypeEnum.sell_empty.code();
        } else if (labelSub.equals(BizTypeEnum.sell_more.msg())) {
            type = BizTypeEnum.sell_more.code();
        } else if (labelSub.equals(BizTypeEnum.buy_empty.msg())) {
            type = BizTypeEnum.buy_empty.code();
        } else if (labelSub.equals(BizTypeEnum.buy_more.msg())) {
            type = BizTypeEnum.buy_more.code();
        } else {
            type = null;
        }
        return type;
    }

    /**
     * 获取合约ID
     *
     * @param sym             合约类型描述
     * @param contractClauses 合约期号
     * @return
     */
    private String getInstrumentId(String sym, String contractClauses) {
        if (StringUtils.isEmpty(sym)) {
            return null;
        }
        String instrument_id;
        if (sym.equals(ContractEnum.BTC_QUARTER.msg()) || sym.equals(ContractEnum.BTC__USDT_QUARTER.msg()) || sym.equals(ContractEnum.BTC__USD_QUARTER.msg() + contractClauses)) {
            if (sym.equals(ContractEnum.BTC_QUARTER.msg())) {
                instrument_id = ContractEnum.BTC_QUARTER.code() + contractClauses;
            } else if (sym.equals(ContractEnum.BTC__USDT_QUARTER.msg())) {
                instrument_id = ContractEnum.BTC__USDT_QUARTER.code() + contractClauses;
            } else {
                instrument_id = null;
            }
        } else if (sym.equals(ContractEnum.ETH_QUARTER.msg()) || sym.equals(ContractEnum.ETH__USDT_QUARTER.msg()) || sym.equals(ContractEnum.ETH__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.ETH__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.LTC_QUARTER.msg()) || sym.equals(ContractEnum.LTC__USDT_QUARTER.msg()) || sym.equals(ContractEnum.LTC__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.LTC__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.ETC_QUARTER.msg()) || sym.equals(ContractEnum.ETC__USDT_QUARTER.msg()) || sym.equals(ContractEnum.ETC__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.ETC__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.XRP_QUARTER.msg()) || sym.equals(ContractEnum.XRP__USDT_QUARTER.msg()) || sym.equals(ContractEnum.XRP__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.XRP__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.EOS_QUARTER.msg()) || sym.equals(ContractEnum.EOS__USDT_QUARTER.msg()) || sym.equals(ContractEnum.EOS__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.EOS__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.BCH_QUARTER.msg()) || sym.equals(ContractEnum.BCH__USDT_QUARTER.msg()) || sym.equals(ContractEnum.BCH__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.BCH__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.BSV_QUARTER.msg()) || sym.equals(ContractEnum.BSV__USDT_QUARTER.msg()) || sym.equals(ContractEnum.BSV__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.BSV__USDT_QUARTER.code() + contractClauses;
        } else if (sym.equals(ContractEnum.TRX_QUARTER.msg()) || sym.equals(ContractEnum.TRX__USDT_QUARTER.msg()) || sym.equals(ContractEnum.TRX__USD_QUARTER.msg() + contractClauses)) {
            instrument_id = ContractEnum.TRX__USDT_QUARTER.code() + contractClauses;
        } else {
            instrument_id = null;
        }
        return instrument_id;
    }

    /**
     * 下单
     *
     * @param order
     * @param firmOfferHis
     */
    private void insert(Order order, FirmOfferHis firmOfferHis) {
        OrderResult orderResult = tradeAPIService.order(order);
        if (orderResult.isResult()) {
            // 下单完成,插入记录
            firmOfferHis.setFollowStatus(0);
            firmOfferHisMapper.insertSelective(firmOfferHis);
            log.info("下单成功..............");
        } else {
            firmOfferHis.setFollowStatus(1);
            firmOfferHisMapper.insertSelective(firmOfferHis);
            log.warn("下单未成功,原因:{}" + orderResult);
        }
    }

    public static void main(String[] args) {
//        try {
//            String url = SysConfInterface.hostAddress + SysConfInterface.latestUrl;
//            Connection connection = Jsoup.connect(url);
//            connection.ignoreContentType(true).ignoreHttpErrors(true).timeout(30000);
//            connection.header("token", SysConfInterface.token);
//            Document document = connection.get();
//            String result = document.getElementsByTag("body").text();
//            System.out.println(result);
//            if (StringUtils.isNotEmpty(result)){
//                JSONObject jsonObject = JSONObject.parseObject(result);
//                JSONArray firmOfferHisList = jsonObject.getJSONObject("data").getJSONArray("firmOfferHisList");
//                List<FirmOfferHis> firmOfferHis = firmOfferHisList.toJavaList(FirmOfferHis.class);
//
//                System.out.println(firmOfferHis);
//            }else{
//                log.warn("查询币coin最新操作数据,未出现数据..........");
//            }
//        }catch (Exception e){
//            log.error("查询币coin最新操作数据,出现异常,异常原因{}",e );
//        }

        String content = "【半木夏】的实盘账户在【OKEx】以单价【204.89USD】【卖出平多】ETH季度合约，挂单【531张】，成交【120张】";
        String substring0 = content.substring(content.indexOf("挂单【") + 3);
        String substring1 = substring0.substring(0, substring0.indexOf("，"));
        String substring2 = substring1.substring(0, substring1.indexOf("张】"));
        System.out.println(substring0);
        System.out.println(substring1);
        System.out.println(substring2);

        System.out.println(content.lastIndexOf("张】"));
        System.out.println(content.lastIndexOf("成交【"));
        System.out.println(content.substring(content.lastIndexOf("成交【") + 3, content.lastIndexOf("张】")));


        //本来想做,检测功能,发现币coin的实盘数据延迟太假,很容易误伤,做不了
//        try {
//
//            /**
//             * 查询自己的仓位
//             */
//            String str1 = "{\n" +
//                    "    \"result\": true,\n" +
//                    "    \"holding\": [\n" +
//                    "        {\n" +
//                    "            \"long_qty\": \"4\",\n" +
//                    "            \"long_avail_qty\": \"4\",\n" +
//                    "            \"long_margin\": \"0.00323844\",\n" +
//                    "            \"long_liqui_price\": \"7762.09\",\n" +
//                    "            \"long_pnl_ratio\": \"0.06052306\",\n" +
//                    "            \"long_avg_cost\": \"8234.43\",\n" +
//                    "            \"long_settlement_price\": \"8234.43\",\n" +
//                    "            \"realised_pnl\": \"-0.00000296\",\n" +
//                    "            \"short_qty\": \"2\",\n" +
//                    "            \"short_avail_qty\": \"2\",\n" +
//                    "            \"short_margin\": \"0.00241105\",\n" +
//                    "            \"short_liqui_price\": \"9166.74\",\n" +
//                    "            \"short_pnl_ratio\": \"0.03318052\",\n" +
//                    "            \"short_avg_cost\": \"8295.13\",\n" +
//                    "            \"short_settlement_price\": \"8295.13\",\n" +
//                    "            \"instrument_id\": \"BTC-USD-191227\",\n" +
//                    "            \"long_leverage\": \"15\",\n" +
//                    "            \"short_leverage\": \"10\",\n" +
//                    "            \"created_at\": \"2019-09-25T07:58:42.129Z\",\n" +
//                    "            \"updated_at\": \"2019-10-08T13:12:09.438Z\",\n" +
//                    "            \"margin_mode\": \"fixed\",\n" +
//                    "            \"short_margin_ratio\": \"0.10292507\",\n" +
//                    "            \"short_maint_margin_ratio\": \"0.005\",\n" +
//                    "            \"short_pnl\": \"7.853E-5\",\n" +
//                    "            \"short_unrealised_pnl\": \"7.853E-5\",\n" +
//                    "            \"long_margin_ratio\": \"0.07103743\",\n" +
//                    "            \"long_maint_margin_ratio\": \"0.005\",\n" +
//                    "            \"long_pnl\": \"1.9841E-4\",\n" +
//                    "            \"long_unrealised_pnl\": \"1.9841E-4\",\n" +
//                    "            \"long_settled_pnl\": \"0\",\n" +
//                    "            \"short_settled_pnl\": \"0\",\n" +
//                    "            \"last\": \"8266.99\"\n" +
//                    "        }\n" +
//                    "    ],\n" +
//                    "    \"margin_mode\": \"fixed\"\n" +
//                    "}";
//            //futuresTradeAPIService.getInstrumentPosition();
//            JSONObject jsonObject1 = JSONObject.parseObject(str1);
//            JSONArray holding = jsonObject1.getJSONArray("holding");
//            BigDecimal long_avail_qty;
//            BigDecimal short_avail_qty;
//            for (int k = 0; k < holding.size(); k++) {
//                JSONObject holdingJSONObject = holding.getJSONObject(k);
//                 long_avail_qty = new BigDecimal(holdingJSONObject.getString("long_avail_qty"));
//                 short_avail_qty = new BigDecimal(holdingJSONObject.getString("short_avail_qty"));
//                break;
//            }
//            /**
//             * 获取跟单用户的仓位
//              */
//            String url = "https://blz.bicoin.com.cn" + "/firmOffer/positions";
//            Connection connection = Jsoup.connect(url);
//            connection.ignoreContentType(true).ignoreHttpErrors(true).timeout(20000);
//            connection.header("token", "2fd19ae6e99c457264617eacbaccee4a");
//            Document document = connection.get();
//            String result = document.getElementsByTag("body").text();
//            JSONObject jsonObject = JSONObject.parseObject(result);
//            JSONArray data = jsonObject.getJSONArray("data");
//            List<String> list = Lists.newArrayList();
//            for (int i = 0; i < data.size(); i++) {
//                JSONObject dataJSONObject = data.getJSONObject(i);
//                Integer userId = dataJSONObject.getInteger("userId");
//                if(userId == 4097){
//                    JSONArray futurePosition = dataJSONObject.getJSONArray("futurePosition");
//                    for (int j = 0; j < futurePosition.size(); j++) {
//                        JSONObject positionJSONObject = futurePosition.getJSONObject(i);
//                        String benifitUnit = positionJSONObject.getString("benifitUnit");
//                        if (benifitUnit.equalsIgnoreCase("ETH")){
//                            BigDecimal availQty = new BigDecimal(positionJSONObject.getString("availQty"));//张数
//                            String type = positionJSONObject.getString("type");//方向
//                            StringBuilder stringBuilder = new StringBuilder();
//                            if (null == availQty || BigDecimal.ZERO.compareTo(availQty) == 0){
//                                stringBuilder.append()
//                            }
//                            list
//                        }
//                    }
//                }
//            }
//            // 跟单用户没有该仓位,则自己必须要平掉仓位
//            if (availQty.compareTo(BigDecimal.ZERO) == 0 || StringUtils.isEmpty(type)){
//                if (){
//
//                }
//            }
//            System.out.println(result);
//        } catch (Exception e) {
//            log.error("查询币coin最新操作数据,出现异常,异常原因{}", e);
//        }
    }


    /**
     * 规则如下:
     * 1.倍数和张数参数必须有
     * 2.直接舍弃小数位
     * 3.金主隐藏,手动处理(邮件通知)
     * 4.无论金主开什么类型的合约,我们使用U本位合约,金主全部平仓后,我们也全部平仓,直接拿着U不动
     * 5.跟单操作,无论是开多还是开空还是平仓,都是直接市价,不看价格
     *
     * @param typeStr 跟单用户id
     */
    @Override
    public void positionSendOrder(String typeStr, String oneselfUserId, BigDecimal scope, Integer scopeNum) {
        if (contentMapper.selectByTime() > scopeNum) {
            DingDingRobotUtils.sendMessage(String.format("5分钟之内,持仓变动超过阀门值(%s)次,已经停止服务,等待人工介入...", scopeNum), true);
            sysConfigMapper.updateFollow(0, 1);
            SysConfInterface.isFollow = false;
            return;
        }

        // 获取金主用户的相关数据
        AccountInfo accountInfo = getPositionData(typeStr);
        if (accountInfo.getFutureShortAmount().compareTo(BigDecimal.ZERO) == 0 && accountInfo.getFutureLongAmount().compareTo(BigDecimal.ZERO) == 0) {
            log.warn(String.format("【币coin金主】 %s 多空都没开,直接结束.....", accountInfo.getNickName()));
            return;
        }
        // 获取用户自身的相关数据
        AccountInfo oneselfAccountInfo = getPositionData(oneselfUserId);
        BigDecimal balance = oneselfAccountInfo.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn(String.format("【自身账户】 用户id: %s 姓名: %s , 账户持仓金额为0....", oneselfUserId, oneselfAccountInfo.getNickName()));
            return;
        }
        if (accountInfo.getFutureShortAmount().compareTo(BigDecimal.ZERO) == 0 && oneselfAccountInfo.getFutureShortAmount().compareTo(BigDecimal.ZERO) == 1) {
            // 空单,市价平仓
            unwind(oneselfAccountInfo.getList(), BizTypeEnum.buy_empty);
            log.warn(String.format("【币coin金主】 %s 空倍为0,自身空倍为 %s ,直接空单,进行市价全平.....", accountInfo.getNickName(), oneselfAccountInfo.getFutureShortAmount()));
            return;
        }
        if (accountInfo.getFutureLongAmount().compareTo(BigDecimal.ZERO) == 0 && oneselfAccountInfo.getFutureLongAmount().compareTo(BigDecimal.ZERO) == 1) {
            // 多单,市价平仓
            unwind(oneselfAccountInfo.getList(), BizTypeEnum.sell_more);
            log.warn(String.format("【币coin金主】 %s 多倍为0,自身多倍为 %s ,直接多单,进行市价全平.....", accountInfo.getNickName(), oneselfAccountInfo.getFutureLongAmount()));
            return;
        }
        // 超过设置的保障范围,多单,进行全部的市价平仓
        if (oneselfAccountInfo.getFutureLongAmount().compareTo(BigDecimal.ZERO) == 1 && accountInfo.getFutureLongAmount().subtract(oneselfAccountInfo.getFutureLongAmount()).abs().compareTo(scope) == 1) {
            // 多单,市价平仓
            unwind(oneselfAccountInfo.getList(), BizTypeEnum.sell_more);
            // 插入持仓变动记录
            String content = String.format("【保障范围】 %s 多倍为 %s ,自身多倍为 %s ,直接多单,进行市价全平.....", accountInfo.getNickName(), accountInfo.getFutureLongAmount(), oneselfAccountInfo.getFutureLongAmount());
            ContentEntity contentEntity = new ContentEntity();
            contentEntity.setMsg(content);
            contentEntity.setCreateAt(new Date());
            contentMapper.insertSelective(contentEntity);
            log.warn(content);
            return;
        }

        // 超过设置的保障范围,空单,进行全部的市价平仓
        if (oneselfAccountInfo.getFutureShortAmount().compareTo(BigDecimal.ZERO) == 1 && accountInfo.getFutureShortAmount().subtract(oneselfAccountInfo.getFutureShortAmount()).abs().compareTo(scope) == 1) {
            // 空单,市价平仓
            unwind(oneselfAccountInfo.getList(), BizTypeEnum.buy_empty);
            // 插入持仓变动记录
            String content = String.format("【保障范围】 %s 空倍为 %s ,自身空倍为 %s ,直接空单,进行市价全平.....", accountInfo.getNickName(), accountInfo.getFutureShortAmount(), oneselfAccountInfo.getFutureShortAmount());
            ContentEntity contentEntity = new ContentEntity();
            contentEntity.setMsg(content);
            contentEntity.setCreateAt(new Date());
            contentMapper.insertSelective(contentEntity);
            log.warn(content);
            return;
        }
        // 判断用户持仓金额是否小于止损金额线
        if (oneselfAccountInfo.getBalance().compareTo(SysConfInterface.stopLossNum) == -1){
            DingDingRobotUtils.sendMessage(String.format("实时持仓金额(%s)已经小于持仓止损线(%s),所以停止服务,等待人工介入...", oneselfAccountInfo.getBalance(),SysConfInterface.stopLossNum), true);
            sysConfigMapper.updateFollow(0, 1);
            SysConfInterface.isFollow = false;
        }
        log.info(String.format("【用户】多倍: %s 空倍: %s", oneselfAccountInfo.getFutureLongAmount(), oneselfAccountInfo.getFutureShortAmount()));
        log.info(String.format("【金主】多倍: %s 空倍: %s", accountInfo.getFutureLongAmount(), accountInfo.getFutureShortAmount()));
        // 遍历金主,持仓数据
        for (Position position : accountInfo.getList()) {
            if (position.getExChange().equalsIgnoreCase("okex")) {
                // 获取张数对应的金额
                BigDecimal qtyBalance;
                if (position.getValueUnit().equalsIgnoreCase("btc")) {
                    qtyBalance = new BigDecimal(100).multiply(new BigDecimal(position.getQty()));
                } else {
                    qtyBalance = new BigDecimal(10).multiply(new BigDecimal(position.getQty()));
                }
                // 计算出占比中的金额
                BigDecimal ratioAmount = BigDecimal.ZERO;
                if (position.getType().equalsIgnoreCase("long")) {
                    BigDecimal divide = qtyBalance.divide(accountInfo.getLongQtyTotal(), 6, BigDecimal.ROUND_DOWN);
                    ratioAmount = balance.multiply(accountInfo.getFutureLongAmount()).multiply(divide);
                }
                if (position.getType().equalsIgnoreCase("short")) {
                    BigDecimal divide = qtyBalance.divide(accountInfo.getShortQtyTotal(), 6, BigDecimal.ROUND_DOWN);
                    ratioAmount = balance.multiply(accountInfo.getFutureShortAmount()).multiply(divide);
                }

                //根据出占比中的金额,计算出下单张数
                String qty = "0";
                if (position.getValueUnit().equalsIgnoreCase("btc")) {
                    qty = ratioAmount.divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP).toPlainString();
                } else {
                    qty = ratioAmount.divide(new BigDecimal(10), 1, BigDecimal.ROUND_HALF_UP).toPlainString();
                }

                if (qty.equalsIgnoreCase("0")) {
                    log.warn("【下单】下单未成功,原因: 计算得出的张数为0");
                    continue;
                }
                /**
                 * 下单
                 */
                sendOrder(qty, position.getType().equalsIgnoreCase("long") ? BizTypeEnum.buy_more : BizTypeEnum.sell_empty, position.getValueUnit());
            } else {
                log.warn("【下单】不处理除okex外的持仓数据");
            }
        }
    }


    /**
     * [持仓]根据请求,拿到数据结果集,二次处理数据
     *
     * @param typeStr 跟单用户id
     * @return
     */
    @Override
    public AccountInfo getPositionData(String typeStr) {
        AccountInfo accountInfo = new AccountInfo();
        String result = getCoinData(typeStr);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        // 账户昵称
        accountInfo.setNickName(data.getString("nickName"));
        // 总余额,单位usdt
        accountInfo.setBalance(data.getBigDecimal("balance"));
        // 倍数: 多
        accountInfo.setFutureLongAmount(data.getBigDecimal("futureLongAmount"));
        accountInfo.setLongBalance(accountInfo.getBalance().multiply(accountInfo.getFutureLongAmount()));
        // 倍数: 空
        accountInfo.setFutureShortAmount(data.getBigDecimal("futureShortAmount"));
        accountInfo.setShortBalance(accountInfo.getBalance().multiply(accountInfo.getFutureShortAmount()));

        /**
         * 持仓数据
         */
        List<Position> list = new ArrayList<>();
        Position position;
        JSONArray futurePosition = data.getJSONArray("futurePosition");
        Integer longTotal = 0;
        Integer shortTotal = 0;
        for (int i = 0; i < futurePosition.size(); i++) {
            position = new Position();
            JSONObject positionJSONObject = futurePosition.getJSONObject(i);
            // 张数
            position.setQty(positionJSONObject.getInteger("qty"));
            // 类型 long: 多  short: 空
            position.setType(positionJSONObject.getString("type"));
            // 币种名: 例如 BTC
            position.setValueUnit(positionJSONObject.getString("valueUnit"));
            // 交易所名称: 例如 Okex
            position.setExChange(positionJSONObject.getString("exChange"));
            list.add(position);

            if (positionJSONObject.getString("type").equalsIgnoreCase("long")) {
                if (positionJSONObject.getString("valueUnit").equalsIgnoreCase("btc")) {
                    longTotal = longTotal + positionJSONObject.getInteger("qty") * 10;
                } else {
                    longTotal = longTotal + positionJSONObject.getInteger("qty");
                }
            }

            if (positionJSONObject.getString("type").equalsIgnoreCase("short")) {
                if (positionJSONObject.getString("valueUnit").equalsIgnoreCase("btc")) {
                    shortTotal = shortTotal + positionJSONObject.getInteger("qty") * 10;
                } else {
                    shortTotal = shortTotal + positionJSONObject.getInteger("qty");
                }
            }
        }
        accountInfo.setList(list);
        accountInfo.setLongQtyTotal(new BigDecimal(10).multiply(new BigDecimal(longTotal)));
        accountInfo.setShortQtyTotal(new BigDecimal(10).multiply(new BigDecimal(shortTotal)));
        return accountInfo;
    }


    /**
     * 市价平仓
     *
     * @param list        持仓数据
     * @param bizTypeEnum 方向
     */
    private void unwind(List<Position> list, BizTypeEnum bizTypeEnum) {
        for (Position position : list) {
            if (position.getExChange().equalsIgnoreCase("okex")) {
                // 获取合约id
                String instrument_id = position.getValueUnit().toUpperCase() + "-USD-" + SysConfInterface.contractClauses;
                closeOut(bizTypeEnum.msg(), instrument_id);
            } else {
                log.warn("【平仓】不处理除okex外的持仓数据");
            }
        }
    }

    /**
     * 市价下单
     *
     * @param qty         张数
     * @param bizTypeEnum 方向
     * @param coin        币种
     */
    private void sendOrder(String qty, BizTypeEnum bizTypeEnum, String coin) {
        Order order = new Order();
        order.setClient_oid(RandomStringUtils.randomAlphabetic(32));
        order.setOrder_type(OrderTypeEnum.MARKET.code());
        // 获取张数
        order.setSize(qty);
        //sell_empty("2", "卖出开空"),sell_more("3", "卖出平多"), buy_empty("4", "买入平空"),buy_more("1", "买入开多"),
        order.setType(bizTypeEnum.code());
        // 获取合约id
        String instrument_id = coin + "-USD-" + SysConfInterface.contractClauses;
        order.setinstrument_id(instrument_id);
        try {
            OrderResult orderResult = tradeAPIService.order(order);
            if (orderResult.isResult()) {
                log.info("【下单】下单成功..............");
            } else {
                log.warn("【下单】下单未成功,原因:{}" + orderResult);
            }
        } catch (Exception e) {
            log.error("【下单】下单操作失败,原因:{}", e.getMessage());
        }

    }

}