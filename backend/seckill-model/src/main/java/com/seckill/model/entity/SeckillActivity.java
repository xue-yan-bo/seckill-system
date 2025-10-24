package com.seckill.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动实体类
 */
@Data
@TableName("tb_seckill_activity")
public class SeckillActivity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 活动ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    
    /**
     * 秒杀库存
     */
    private Integer seckillStock;
    
    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态（0未开始 1进行中 2已结束）
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

