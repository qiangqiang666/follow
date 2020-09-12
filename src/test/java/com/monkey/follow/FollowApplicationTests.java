package com.monkey.follow;

import com.alibaba.fastjson.JSONObject;
import com.monkey.follow.okex.APIConfiguration;
import com.monkey.follow.okex.OkUtil;
import com.monkey.follow.okex.bean.spot.result.ServerTimeDto;
import com.monkey.follow.okex.futures.param.ClosePositions;
import com.monkey.follow.okex.futures.param.Order;
import com.monkey.follow.okex.futures.result.Instruments;
import com.monkey.follow.okex.futures.result.OrderResult;
import com.monkey.follow.okex.futures.result.ServerTime;
import com.monkey.follow.okex.service.GeneralAPIService;
import com.monkey.follow.okex.service.account.AccountAPIService;
import com.monkey.follow.okex.service.futures.FuturesMarketAPIService;
import com.monkey.follow.okex.service.futures.FuturesTradeAPIService;
import com.monkey.follow.okex.service.futures.impl.GeneralAPIServiceImpl;
import com.monkey.follow.okex.service.spot.SpotAccountAPIService;
import com.monkey.follow.service.CoinService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;


@SpringBootTest
class FollowApplicationTests {

    @Autowired
    private CoinService coinService;
    @Autowired
    private GeneralAPIService generalAPIService;
    @Autowired
    private SpotAccountAPIService spotAccountAPIService;
    @Test
    void contextLoads() {
        coinService.getLatestOperation("4097");
    }

    @Autowired
    private FuturesTradeAPIService tradeAPIService;
    @Test
    public void testOrder() {
        Order order = new Order();
        order.setinstrument_id("TBTC-USDT-200626");
        order.setClient_oid(RandomStringUtils.randomAlphabetic(32));
        order.setType("1");
        order.setSize("1");
        order.setOrder_type("4");
        try {
            OrderResult result = tradeAPIService.order(order);
            System.out.println(result.isResult());
            System.out.println(result.getError_code());
        }catch (Exception e){
            //32015 : Risk rate lower than 100% before opening position
            //32015 : Risk rate lower than 100% before opening position
            //32014 : Positions that you are closing exceeded the total qty of contracts allowed to close
            //32014 平仓张数大于可平
            //32015 代表可开张数不足
            System.out.println(e.getMessage());
            if (e.getMessage().equalsIgnoreCase("32014 : Positions that you are closing exceeded the total qty of contracts allowed to close")){
                //市价全平
                testOrder2();
            }
        }
    }

    @Autowired
    private FuturesTradeAPIService futuresTradeAPIService;
    @Test
    public void testOrder2() {
        ClosePositions closePositions = new ClosePositions();
        closePositions.setInstrument_id("TBTC-USDT-200626");
        // 平多
        closePositions.setDirection("long");
        // 平空
        //closePositions.setDirection("short");
        futuresTradeAPIService.closePositions(closePositions);
    }
    @Autowired
    private FuturesMarketAPIService futuresMarketAPIService;
    @Test
    public void testList() {
        List<Instruments> instruments = futuresMarketAPIService.getInstruments();
        instruments.forEach(Instruments-> System.out.println(Instruments));
    }
    @Test
    public void testList1() {
        ServerTimeDto time = spotAccountAPIService.time();
        System.out.println(time);
    }
    @Autowired
    private AccountAPIService accountAPIService;
    @Test
    public void testList11() {
        try {
            for (int i = 0; i < 2; i++) {
                JSONObject usd = accountAPIService.getAllAccount("3", "USD");
                BigDecimal balance = usd.getBigDecimal("balance");
                System.out.println(balance);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

}


}
