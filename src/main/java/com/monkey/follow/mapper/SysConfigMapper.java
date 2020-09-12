package com.monkey.follow.mapper;

import com.monkey.follow.MyMapper;
import com.monkey.follow.model.FirmOfferHis;
import com.monkey.follow.model.SysConfig;
import org.apache.ibatis.annotations.Param;


public interface SysConfigMapper extends MyMapper<SysConfig> {

    SysConfig selectByType(@Param("type") Integer type);

    void updateNum(@Param("id") Integer id);
}