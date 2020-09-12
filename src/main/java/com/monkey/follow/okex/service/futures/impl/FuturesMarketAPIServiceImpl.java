package com.monkey.follow.okex.service.futures.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.follow.okex.APIConfiguration;
import com.monkey.follow.okex.client.APIClient;
import com.monkey.follow.okex.futures.result.*;
import com.monkey.follow.okex.service.futures.FuturesMarketAPIService;

import java.util.List;

/**
 * Futures market api
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/3/9 16:09
 */
public class FuturesMarketAPIServiceImpl implements FuturesMarketAPIService {

    private APIClient client;
    private FuturesMarketAPI api;

    public FuturesMarketAPIServiceImpl(APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = client.createService(FuturesMarketAPI.class);
    }

    @Override
    public List<Instruments> getInstruments() {
        return this.client.executeSync(this.api.getInstruments());
    }

    @Override
    public List<Currencies> getCurrencies() {
        return this.client.executeSync(this.api.getCurrencies());
    }

    @Override
    public Book getInstrumentBook(String instrument_id,  String size,String depth) {
        return this.client.executeSync(this.api.getInstrumentBook(instrument_id, size,depth));
    }

    @Override
    public Ticker getInstrumentTicker(String instrument_id) {
        return this.client.executeSync(this.api.getInstrumentTicker(instrument_id));
    }

    @Override
    public List<Ticker> getAllInstrumentTicker() {
        return this.client.executeSync(this.api.getAllInstrumentTicker());
    }

    @Override
    public List<Trades> getInstrumentTrades(String instrument_id, String after, String before, String limit) {
        return this.client.executeSync(this.api.getInstrumentTrades(instrument_id,  after,  before,  limit));
    }

    @Override
    public JSONArray getInstrumentCandles(String instrument_id, String start, String end, String granularity) {
        return this.client.executeSync(this.api.getInstrumentCandles(instrument_id, start,end, granularity));
    }

    @Override
    public Index getInstrumentIndex(String instrument_id) {
        return this.client.executeSync(this.api.getInstrumentIndex(instrument_id));
    }

    @Override
    public ExchangeRate getExchangeRate() {
        return this.client.executeSync(this.api.getExchangeRate());
    }


    @Override
    public EstimatedPrice getInstrumentEstimatedPrice(String instrument_id) {
        return this.client.executeSync(this.api.getInstrumentEstimatedPrice(instrument_id));
    }

    @Override
    public Holds getInstrumentHolds(String instrument_id) {
        return this.client.executeSync(this.api.getInstrumentHolds(instrument_id));
    }

    @Override
    public PriceLimit getInstrumentPriceLimit(String instrument_id) {
        return this.client.executeSync(this.api.getInstrumentPriceLimit(instrument_id));
    }

    @Override
    public List<Liquidation> getInstrumentLiquidation(String instrument_id, String status, String from, String to, String limit) {
        return this.client.executeSync(this.api.getInstrumentLiquidation(instrument_id, status,  from,  to,  limit));
    }

    @Override
    public JSONObject getMarkPrice(String instrumentId){
        return this.client.executeSync(this.api.getMarkPrice(instrumentId));
    }

    @Override
    public JSONArray getSettlementHistory(String instrument_id, String start, String limit, String end) {
        return this.client.executeSync(this.api.getSettlementHistory(instrument_id,start,limit,end));
    }


}
