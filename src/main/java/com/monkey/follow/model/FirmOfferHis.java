package com.monkey.follow.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 币coin最新操作实体
 */
@Data
@Table(name="t_his")
public class FirmOfferHis {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 用户昵称
     */
    private String label;
    /**
     * 操作描述
     */
    private String labelSub;
    /**
     * 发送时间(单位时间戳)
     */

    private Long sendTime;
    /**
     * 合约类型描述
      */
    private String sym;
    /**
     * 描述标题
      */
    private String title;
    /**
     * 描述内容
      */

    private String content;
    /**
     * 单价
     */
    private BigDecimal unit;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 交易所名称
     */
    private String exch;
    /**
     * 创建记录时间
     */
    private Date createAt;
    /**
     * 跟单状态(0-成功,1-失败)
     */
    private Integer followStatus;
}
