package com.seckill.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.model.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 分页查询商品列表
     */
    Page<Product> getProductList(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 扣减库存
     */
    boolean deductStock(Long productId, Integer quantity);
    
    /**
     * 增加库存（回滚用）
     */
    boolean addStock(Long productId, Integer quantity);
    
    /**
     * 从缓存获取商品信息
     */
    Product getProductFromCache(Long productId);
}

