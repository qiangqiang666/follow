package com.monkey.follow.okex.service.spot.impl;


import com.monkey.follow.okex.APIConfiguration;
import com.monkey.follow.okex.client.APIClient;
import com.monkey.follow.okex.service.spot.MarginProductAPIService;

public class MarginProductAPIServiceImpl implements MarginProductAPIService {
    private final APIClient client;
    private final MarginProductAPI marginProductAPI;

    public MarginProductAPIServiceImpl(final APIConfiguration config) {
        this.client = new APIClient(config);
        this.marginProductAPI = this.client.createService(MarginProductAPI.class);
    }

    @Override
    public String getMarginMarkPrice(String instrument_id) {
        return this.client.executeSync(this.marginProductAPI.getMarginMarkPrice(instrument_id));

    }

}