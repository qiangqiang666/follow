package com.monkey.follow.mapper;

import com.monkey.follow.MyMapper;
import com.monkey.follow.model.FirmOfferHis;
import org.apache.ibatis.annotations.Param;


public interface FirmOfferHisMapper extends MyMapper<FirmOfferHis> {

    FirmOfferHis selectByMsgId(@Param("msgId") String msgId);
}