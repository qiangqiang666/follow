package com.monkey.follow.okex.service.spot;

import com.monkey.follow.okex.bean.spot.param.OrderAlgoParam;
import com.monkey.follow.okex.bean.spot.param.OrderParamDto;
import com.monkey.follow.okex.bean.spot.param.PlaceOrderParam;
import com.monkey.follow.okex.bean.spot.result.*;
import retrofit2.http.Body;

import java.util.List;
import java.util.Map;

public interface SpotOrderAPIServive {
    /**
     * 添加订单
     *
     * @param order
     * @return
     */
    OrderResult addOrder(PlaceOrderParam order);

    /**
     * 批量下单
     *
     * @param order
     * @return
     */
    Map<String, List<OrderResult>> addOrders(List<PlaceOrderParam> order);

    /**
     * 取消单个订单 delete协议
     *
     *  @param order
     * @param order_id
     */
    OrderResult cancleOrderByOrderId(final PlaceOrderParam order, String order_id);

    /**
     * 取消单个订单 post协议
     *
     * @param order
     * @param order_id
     */
    OrderResult cancleOrderByOrderId_post(final PlaceOrderParam order, String order_id);
    //通过client_oid进行撤单
    OrderResult cancleOrderByClientOid(final PlaceOrderParam order, String client_oid);

    /**
     * 批量取消订单 delete协议
     *
     * @param cancleOrders
     * @return
     */
    Map<String, BatchOrdersResult> cancleOrders(final List<OrderParamDto> cancleOrders);

    /**
     * 批量取消订单 post协议
     *
     * @param cancleOrders
     * @return
     */
    Map<String, Object> cancleOrders_post(final List<OrderParamDto> cancleOrders);

    Map<String, Object> batchCancleOrders_2(final List<OrderParamDto> cancleOrders);

    Map<String, Object> batch_orderCle(List<OrderParamDto> orderParamDto);

    /**
     * 单个订单
     * @param instrument_id
     * @param order_id
     * @return
     */
    OrderInfo getOrderByOrderId(String instrument_id, String order_id);
    OrderInfo getOrderByClientOid(String instrument_id, String client_oid);

    /**
     * 订单列表
     *
     * @param instrument_id
     * @param state
     * @param after
     * @param before
     * @param limit
     * @return
     */
    List<OrderInfo> getOrders(String instrument_id, String state, String after, String before, String limit);

    /**
     * 订单列表
     *
     * @param before
     * @param after
     * @param limit
     * @return
     */
    List<PendingOrdersInfo> getPendingOrders(String before, String after, String limit, String instrument_id);

    /**
     * 账单列表
     *
     * @param order_id
     * @param instrument_id
     * @param before
     * @param after
     * @param limit
     * @return
     */
    List<Fills> getFills(String order_id, String instrument_id, String before, String after, String limit);

    /**
     * 委托下单
     * @param order
     * @return
     */
    OrderAlgoResult addorder_algo(@Body OrderAlgoParam order);

    /**
     * 委托策略撤单
     * @param order
     * @return
     */
    OrderAlgoResult cancelOrder_algo(@Body OrderAlgoParam order);

    /**
     * 查询委托订单
     * @param instrument_id
     * @param order_type
     * @param status
     * @param algo_id
     * @param before
     * @param after
     * @param limit
     * @return
     */
    String  getAlgoOrder(String instrument_id, String order_type, String status, String algo_id, String before, String after, String limit);



}
