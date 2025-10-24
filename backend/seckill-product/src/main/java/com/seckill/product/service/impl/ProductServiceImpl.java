package com.seckill.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.utils.RedisUtil;
import com.seckill.model.entity.Product;
import com.seckill.product.mapper.ProductMapper;
import com.seckill.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    private static final String PRODUCT_CACHE_KEY = "product:info:";
    private static final int CACHE_EXPIRE_TIME = 30; // 缓存过期时间（分钟）
    
    @Override
    public Page<Product> getProductList(Integer pageNum, Integer pageSize, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword);
        }
        
        // 只查询上架的商品
        queryWrapper.eq(Product::getStatus, 1);
        queryWrapper.orderByDesc(Product::getCreateTime);
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }
        
        // 查询商品
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查库存
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }
        
        // 使用乐观锁扣减库存
        int result = baseMapper.deductStock(productId, quantity, product.getVersion());
        if (result == 0) {
            log.warn("商品库存扣减失败，可能库存不足或版本号不匹配 - 商品ID: {}", productId);
            throw new BusinessException("库存扣减失败，请重试");
        }
        
        // 删除缓存
        redisUtil.delete(PRODUCT_CACHE_KEY + productId);
        
        log.info("商品库存扣减成功 - 商品ID: {}, 扣减数量: {}", productId, quantity);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("回滚数量必须大于0");
        }
        
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        product.setStock(product.getStock() + quantity);
        boolean result = this.updateById(product);
        
        // 删除缓存
        redisUtil.delete(PRODUCT_CACHE_KEY + productId);
        
        log.info("商品库存回滚成功 - 商品ID: {}, 回滚数量: {}", productId, quantity);
        return result;
    }
    
    @Override
    public Product getProductFromCache(Long productId) {
        // 先从缓存获取
        String cacheKey = PRODUCT_CACHE_KEY + productId;
        Object cacheData = redisUtil.get(cacheKey);
        
        if (cacheData != null) {
            log.info("从缓存获取商品信息 - 商品ID: {}", productId);
            return JSON.parseObject(cacheData.toString(), Product.class);
        }
        
        // 缓存未命中，从数据库查询
        Product product = this.getById(productId);
        if (product != null) {
            // 存入缓存
            redisUtil.set(cacheKey, JSON.toJSONString(product), CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("商品信息已缓存 - 商品ID: {}", productId);
        }
        
        return product;
    }
}

