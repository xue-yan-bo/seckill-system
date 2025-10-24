package com.seckill.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 扣减库存（乐观锁）
     */
    @Update("UPDATE tb_product SET stock = stock - #{quantity}, version = version + 1 " +
            "WHERE id = #{id} AND stock >= #{quantity} AND version = #{version} AND deleted = 0")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);
}

