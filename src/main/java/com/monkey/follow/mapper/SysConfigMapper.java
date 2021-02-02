package com.monkey.follow.mapper;

import com.monkey.follow.MyMapper;
import com.monkey.follow.model.FirmOfferHis;
import com.monkey.follow.model.SysConfig;
import org.apache.ibatis.annotations.Param;


public interface SysConfigMapper extends MyMapper<SysConfig> {


    void updateNum(@Param("id") Integer id);

    void updateScope(@Param("id") Integer id);

    void updateFollow(@Param("isFollow")int isFollow,@Param("id") Integer id);
}