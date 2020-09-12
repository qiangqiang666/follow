package com.monkey.follow;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;


/**
 * The interface My mapper.
 *
 * @param <T> the type parameter
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
