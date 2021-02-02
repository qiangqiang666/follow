package com.monkey.follow.task;

import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 *
 * @author Monkey
 * @create 2020/4/4 14:10
 */
@Slf4j
@Component
public class ATask {

    @Autowired
    private CoinService coinService;

//    /**
//     * 跟单操作
//     */
//    @Scheduled(cron = "*/1 * * * * ?")
//    public void send() {
//        coinService.getLatestOperation(SysConfInterface.typeStr);
//        log.info("The ATask.send task end");
//    }
//
//    /**
//     * 止损操作
//     */
//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void stopLoss() {
//        coinService.stopLoss("3","USD");
//        log.info("The ATask.stopLoss task end");
//    }

    /**
     * 检测遗留仓位,币coin的实盘数据延迟太高,很容易误伤,做不了
     */
//    @Scheduled(cron = "0/30 * * * * ?")
//    public void legacy() {
//        coinService.legacy(SysConfInterface.typeStr);
//        log.info("The ATask.legacy task end");
//    }
}