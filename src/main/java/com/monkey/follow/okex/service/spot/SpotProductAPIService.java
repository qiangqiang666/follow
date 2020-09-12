package com.monkey.follow.okex.service.spot;

import com.alibaba.fastjson.JSONArray;
import com.monkey.follow.okex.bean.spot.result.*;

import java.util.List;

public interface SpotProductAPIService {

    /**
     * 单个币对行情
     *
     * @param instrument_id
     * @return
     */
    Ticker getTickerByProductId(String instrument_id);

    /**
     *
     * 行情列表
     *
     * @return
     */
    //List<Ticker> getTickers();
    String getTickers();

    List<Ticker> getTickers1();

    /**
     * @param instrument_id
     * @param size
     * @param depth
     * @return
     */
    Book bookProductsByProductId(String instrument_id, String size, String depth);

    /**
     * 币对列表S
     *
     * @return
     */
    List<Product> getProducts();

    /**
     * 交易列表
     *
     * @param instrument_id
     * @param limit
     * @return
     */
    List<Trade> getTrades(String instrument_id, String limit);

    /**
     * @param instrument_id
     * @param granularity
     * @param start
     * @param end
     * @return
     */
    JSONArray getCandles(String instrument_id, String granularity, String start, String end);

    List<String[]> getCandles_1(String product, String granularity, String start, String end);





}
