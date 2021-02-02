package com.monkey.follow.controller;

import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.model.SysConfig;
import com.monkey.follow.service.SysConfigService;
import com.monkey.follow.utils.DingDingRobotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 * 〈〉
 *
 * @author Monkey
 * @create 2020/5/10 22:03
 */
@Controller
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/index")
    public ModelAndView getIndex() {
        SysConfig sysConfig = sysConfigService.updateInitConf();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("txt");
        modelAndView.addObject("scope", sysConfig.getScope());
        modelAndView.addObject("scopeNum", sysConfig.getScopeNum());
        modelAndView.addObject("stopLossNum", sysConfig.getStopLossNum());
        modelAndView.addObject("timeNum", getContentTimeNum());
        return modelAndView;
    }

    @RequestMapping("/init")
    @ResponseBody
    public SysConfig updateInitConfig(){
        return sysConfigService.updateInitConf();
    }

    @RequestMapping("/start")
    @ResponseBody
    public SysConfig updateFollow(){
        SysConfig sysConfig = new SysConfig();
        sysConfig.setId(1);
        sysConfig.setIsFollow(true);
        sysConfigService.updateConf(sysConfig);
        DingDingRobotUtils.sendMessage("服务已经恢复.....", true);
        return sysConfigService.updateInitConf();
    }

    @RequestMapping("/stop")
    @ResponseBody
    public SysConfig updateStop(){
        SysConfig sysConfig = new SysConfig();
        sysConfig.setId(1);
        sysConfig.setIsFollow(false);
        sysConfigService.updateConf(sysConfig);
        DingDingRobotUtils.sendMessage("服务已经停止.....", true);
        return sysConfigService.updateInitConf();
    }

    @PostMapping("/update")
    public String updateConf(@RequestParam BigDecimal scope, @RequestParam Integer scopeNum,@RequestParam BigDecimal stopLossNum){
        SysConfig sysConfigUpdate = new SysConfig();
        sysConfigUpdate.setId(1);
        sysConfigUpdate.setScope(scope);
        sysConfigUpdate.setScopeNum(scopeNum);
        sysConfigUpdate.setStopLossNum(stopLossNum);
        sysConfigService.updateConf(sysConfigUpdate);
        return "redirect:/index";
    }
    @RequestMapping("/count")
    @ResponseBody
    public Map<String,Object> countTime(){
        Map<String,Object> map = new HashMap<>();
        map.put("timeNum", getContentTimeNum());
        map.put("status", SysConfInterface.isFollow ? "服务中":"已停止");
        return map;
    }
    @RequestMapping("/clear")
    @ResponseBody
    public void clearTime(){
        sysConfigService.clearTime();
    }
    /**
     * 实时持仓变动
     * @return
     */
    private Integer getContentTimeNum(){
        return sysConfigService.selectContentNum();
    }


}