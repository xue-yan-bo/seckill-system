package com.seckill.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}

