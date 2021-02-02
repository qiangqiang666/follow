package com.monkey.follow.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * 描述:
 * 〈钉钉自定义机器人〉
 *
 * @author Monkey
 * @create 2021/2/2 15:27
 */
@Slf4j
public class DingDingRobotUtils {


    /**
     * 发送信息
     * @param content 文本内容
     * @param isAtAll 是否通知全部,true-是 false-否
     */
    public static void sendMessage(String content,boolean isAtAll){
        Map<String,Object> map = Maps.newHashMap();
        map.put("msgtype", "text");
        Map<String,String> contentMap = Maps.newHashMap();
        contentMap.put("content", content);
        map.put("text", contentMap);
        Map<String,Object> atMap = Maps.newHashMap();
        String[] strArray = {""};
        atMap.put("atMobiles", strArray);
        atMap.put("isAtAll", isAtAll);
        map.put("at", atMap);
        try {
            Document document = JsoupUtil.sendPostByBody("https://oapi.dingtalk.com/robot/send?access_token=bc6c5a444cc6ed2aa7455c9718c32d891e04dbc74c2ffe93df868732df2a95c6", 10000, JSONObject.toJSONString(map));
            String body = document.getElementsByTag("body").text();
            JSONObject jsonObject = JSONObject.parseObject(body);
            if(jsonObject.getInteger("errcode") == 0){
                log.warn(String.format("【DING】 调用钉钉服务成功,返回结果: %s",body));
            }else{
                log.error(String.format("【DING】 调用钉钉服务失败,返回结果: %s",body));
            }
        }catch (Exception e){
            log.error(String.format("【DING】 调用钉钉,发生异常,原因: %s.....",e.getMessage()));
        }
    }
}