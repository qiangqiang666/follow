package com.monkey.follow.task;

import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 〈策略2: 根据币coin中金主的倍数和张数来进行跟随〉
 *
 * @author Monkey
 * @create 2021/1/29 16:32
 */
@Slf4j
@Component
public class BTask {
    @Autowired
    private CoinService coinService;

    /**
     * 跟单操作
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void position() {
//        log.info("The BTask.position task start");
//        if (SysConfInterface.isFollow) {
//            log.info("【定时任务】跟单中......");
//            coinService.positionSendOrder(SysConfInterface.typeStr,SysConfInterface.oneselfUserId,SysConfInterface.scope,SysConfInterface.scopeNum);
//        }else{
//            log.info("【定时任务】跟单停止.....");
//        }

    }

}