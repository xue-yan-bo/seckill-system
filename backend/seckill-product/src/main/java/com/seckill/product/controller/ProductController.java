package com.seckill.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.annotation.SystemLog;
import com.seckill.common.result.Result;
import com.seckill.model.entity.Product;
import com.seckill.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    @SystemLog(value = "查询商品列表", businessType = 0)
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<Product> page = productService.getProductList(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询商品详情
     */
    @GetMapping("/{id}")
    @SystemLog(value = "查询商品详情", businessType = 0)
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductFromCache(id);
        return Result.success(product);
    }
    
    /**
     * 添加商品
     */
    @PostMapping
    @SystemLog(value = "添加商品", businessType = 1)
    public Result<?> addProduct(@RequestBody Product product) {
        productService.save(product);
        return Result.success("添加成功");
    }
    
    /**
     * 更新商品
     */
    @PutMapping
    @SystemLog(value = "更新商品", businessType = 2)
    public Result<?> updateProduct(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success("更新成功");
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    @SystemLog(value = "删除商品", businessType = 3)
    public Result<?> deleteProduct(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success("删除成功");
    }
}

