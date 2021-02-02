package com.monkey.follow.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述:
 * 〈持仓变动表〉
 *
 * @author Monkey
 * @create 2021/2/1 17:31
 */
@Data
@Table(name = "t_content")
public class ContentEntity {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 内容
     */
    private String msg;

    /**
     * 创建时间
     */
    private Date createAt;
}