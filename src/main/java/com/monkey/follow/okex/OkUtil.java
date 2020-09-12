package com.monkey.follow.okex;

import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.okex.service.GeneralAPIService;
import com.monkey.follow.okex.service.account.AccountAPIService;
import com.monkey.follow.okex.service.account.impl.AccountAPIServiceImpl;
import com.monkey.follow.okex.service.futures.FuturesMarketAPIService;
import com.monkey.follow.okex.service.futures.FuturesTradeAPIService;
import com.monkey.follow.okex.service.futures.impl.FuturesMarketAPIServiceImpl;
import com.monkey.follow.okex.service.futures.impl.FuturesTradeAPIServiceImpl;
import com.monkey.follow.okex.service.futures.impl.GeneralAPIServiceImpl;
import com.monkey.follow.okex.service.spot.SpotAccountAPIService;
import com.monkey.follow.okex.service.spot.impl.SpotAccountAPIServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 〈okex的配置中心〉
 *
 * @author Monkey
 * @create 2020/5/6 20:45
 */
@Component
public class OkUtil {

    /**
     * 1.secretKey,api注册成功后页面上有
     * 2.Passphrase忘记后无法找回
     */
    @Bean
    public APIConfiguration okexApiConfig() {
        APIConfiguration config = new APIConfiguration();
        config.setEndpoint(SysConfInterface.address);
        config.setApiKey(SysConfInterface.apiKey);
        config.setSecretKey(SysConfInterface.secretKey);
        config.setPassphrase(SysConfInterface.passphrase);
        config.setPrint(true);
        return config;
    }

    @Bean
    public GeneralAPIService generalAPIService(APIConfiguration config) {
        return new GeneralAPIServiceImpl(config);
    }

    @Bean
    public SpotAccountAPIService spotAccountAPIService(APIConfiguration config) {
        return new SpotAccountAPIServiceImpl(config);
    }

    @Bean
    public FuturesTradeAPIService futuresTradeAPIService(APIConfiguration config) {
        return new FuturesTradeAPIServiceImpl(config);
    }

    @Bean
    public FuturesMarketAPIService futuresMarketAPIService(APIConfiguration config) {
        return new FuturesMarketAPIServiceImpl(config);
    }

    @Bean
    public AccountAPIService accountAPIService(APIConfiguration config) {
        return new AccountAPIServiceImpl(config);
    }

}