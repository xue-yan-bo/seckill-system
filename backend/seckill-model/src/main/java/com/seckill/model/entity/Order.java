package com.seckill.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("tb_order")
public class Order implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 秒杀活动ID
     */
    private Long activityId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 商品单价
     */
    private BigDecimal price;
    
    /**
     * 订单总价
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态（0待支付 1已支付 2已取消 3已完成）
     */
    private Integer status;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除（0未删除 1已删除）
     */
    @TableLogic
    private Integer deleted;
}

