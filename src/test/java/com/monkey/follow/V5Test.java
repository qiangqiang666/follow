package com.monkey.follow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.model.jsoup.AccountInfo;
import com.monkey.follow.model.jsoup.Position;
import com.monkey.follow.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 〈okex v5Api〉
 *
 * @author Monkey
 * @create 2021/1/29 10:25
 */
@Slf4j
@SpringBootTest
public class V5Test {
    @Autowired
    private CoinService coinService;
    @Test
    void test1() {

        if (SysConfInterface.isFollow) {
            log.info("跟单中......");
            coinService.positionSendOrder(SysConfInterface.typeStr,SysConfInterface.oneselfUserId,SysConfInterface.scope,SysConfInterface.scopeNum);
        }
//        AccountInfo positionData = coinService.getPositionData("170707");
//        System.out.println(JSONObject.toJSONString(positionData));
    }

}