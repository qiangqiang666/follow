package com.monkey.follow.mapper;

import com.monkey.follow.MyMapper;
import com.monkey.follow.model.ContentEntity;


public interface ContentMapper extends MyMapper<ContentEntity> {

    int selectByTime();

    void deleteByTime();

}