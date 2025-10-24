package com.seckill.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.model.entity.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 秒杀活动Mapper接口
 */
@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {
    
    /**
     * 扣减秒杀库存（乐观锁）
     */
    @Update("UPDATE tb_seckill_activity SET seckill_stock = seckill_stock - #{quantity}, version = version + 1 " +
            "WHERE id = #{id} AND seckill_stock >= #{quantity} AND version = #{version} AND deleted = 0")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);
}

