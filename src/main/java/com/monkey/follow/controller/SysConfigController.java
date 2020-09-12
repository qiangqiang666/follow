package com.monkey.follow.controller;

import com.monkey.follow.model.SysConfig;
import com.monkey.follow.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 〈〉
 *
 * @author Monkey
 * @create 2020/5/10 22:03
 */
@RestController
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @RequestMapping("/init")
    public SysConfig updateInitConfig(){
        return sysConfigService.updateInitConf();
    }
}