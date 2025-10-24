package com.seckill.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("tb_product")
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 商品图片
     */
    private String image;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 状态（0下架 1上架）
     */
    private Integer status;
    
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

