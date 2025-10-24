# 🔧 Mock API 服务器

## 📂 这是临时的Mock服务器

> ⚠️ 用于快速体验，不包含真实的高并发功能

## 🎯 用途

在真实后端服务未启动前，提供API接口让前端能正常运行

## 🚀 启动方式

```bash
npm install    # 安装依赖（首次）
npm start      # 启动Mock服务器
```

**端口**：8083

## ✅ 支持的API

- POST `/api/user/login` - 用户登录
- POST `/api/user/register` - 用户注册
- GET `/api/user/info` - 获取用户信息
- GET `/api/product/list` - 商品列表
- GET `/api/product/:id` - 商品详情
- GET `/api/seckill/list` - 秒杀活动列表
- POST `/api/seckill/:id` - 参与秒杀
- GET `/api/order/list` - 订单列表
- POST `/api/order/pay/:id` - 支付订单
- POST `/api/order/cancel/:id` - 取消订单

## 💾 数据存储

- 数据存储在内存中
- 重启后数据会重置
- 不连接数据库

## ⚠️ 注意事项

1. **仅用于演示**，不包含真实的高并发处理
2. 当真实后端服务启动后，应停止此Mock服务器
3. Mock服务器与秒杀服务使用相同端口（8083）

## 🔄 切换到真实后端

1. 停止Mock服务器（关闭PowerShell窗口）
2. 启动MySQL、Redis、RabbitMQ
3. 启动真实的后端服务
4. 前端无需修改，自动连接真实后端

