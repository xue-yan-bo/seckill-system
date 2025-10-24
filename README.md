# 🛒 电商秒杀系统

> 基于 Spring Boot + Vue3 的高并发电商秒杀系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.14-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.3.4-4FC08D.svg)](https://vuejs.org/)
[![Redis](https://img.shields.io/badge/Redis-5.0+-red.svg)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📖 项目简介

这是一个完整的电商秒杀系统，采用前后端分离架构，实现了用户管理、商品管理、秒杀活动、订单管理、二维码支付等核心功能。项目注重高并发场景下的性能优化，使用了多种技术手段保证系统的稳定性和可靠性。

### ✨ 核心功能

- 👤 **用户管理**：注册、登录、JWT认证、个人信息管理
- 📦 **商品管理**：商品列表、详情查询、分页搜索
- ⚡ **秒杀系统**：秒杀活动、限流、防超卖、库存预热
- 📋 **订单管理**：订单创建、支付、取消、状态管理
- 💳 **支付功能**：二维码支付、支付状态轮询、自动回调

### 🎯 技术亮点

- 🔒 **分布式锁**：基于 Redisson 实现分布式锁，防止库存超卖
- 🚦 **接口限流**：自定义限流注解 + AOP + Redis，防止恶意刷单
- ⚡ **缓存优化**：Redis 缓存预热，热点数据预加载
- 🔐 **乐观锁**：MyBatis Plus @Version 注解，数据库层面防并发
- 📨 **异步处理**：RabbitMQ 消息队列，订单异步创建
- 🎨 **前后端分离**：RESTful API，支持分布式部署

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.14 | 核心框架 |
| MyBatis Plus | 3.5.3 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 5.0+ | 缓存数据库 |
| RabbitMQ | 3.8+ | 消息队列 |
| Redisson | 3.x | 分布式锁 |
| JWT | - | Token 认证 |
| Druid | - | 数据库连接池 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.3.4 | 前端框架 |
| Vite | 4.4.9 | 构建工具 |
| Pinia | 2.1.6 | 状态管理 |
| Vue Router | 4.2.4 | 路由管理 |
| Element Plus | 2.3.14 | UI 组件库 |
| Axios | 1.5.0 | HTTP 请求 |

## 📁 项目结构

```
seckill-system/
├── backend/                    # 后端项目
│   ├── seckill-common/        # 公共模块（注解、切面、工具类）
│   ├── seckill-model/         # 实体类模块
│   ├── seckill-user/          # 用户服务（8081）
│   ├── seckill-product/       # 商品服务（8082）
│   ├── seckill-seckill/       # 秒杀服务（8083）
│   └── seckill-order/         # 订单服务（8084）
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── utils/             # 工具类
│   │   └── views/             # 页面组件
│   └── package.json
├── database/                   # 数据库脚本
│   └── init.sql               # 初始化脚本
├── mock-server/               # Mock API 服务器
└── README.md                  # 项目文档
```

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- RabbitMQ 3.8+
- Node.js 16+

### 启动步骤

#### 1. 克隆项目

```bash
git clone https://github.com/your-username/seckill-system.git
cd seckill-system
```

#### 2. 数据库初始化

```bash
mysql -u root -p < database/init.sql
```

#### 3. 启动 Redis

```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server
```

#### 4. 启动 RabbitMQ

```bash
# Windows
rabbitmq-server.bat

# Linux/Mac
rabbitmq-server
```

#### 5. 启动后端服务

```bash
# 用户服务
cd backend/seckill-user
mvn spring-boot:run

# 商品服务
cd backend/seckill-product
mvn spring-boot:run

# 秒杀服务
cd backend/seckill-seckill
mvn spring-boot:run

# 订单服务
cd backend/seckill-order
mvn spring-boot:run
```

#### 6. 启动前端

```bash
cd frontend
npm install
npm run dev
```

#### 7. 访问系统

浏览器打开：http://localhost:3000

**测试账号**：
- 用户名：`admin`
- 密码：`123456`

### 快速演示（无需配置数据库）

如果暂时无法配置完整环境，可以使用 Mock 服务器快速体验：

```bash
# 启动 Mock API 服务器
cd mock-server
npm install
npm start

# 启动前端
cd frontend
npm install
npm run dev
```

## 💡 核心功能展示

### 1. 分布式锁防超卖

```java
@RateLimit(key = "seckill", time = 1, count = 100)
public Result seckill(Long activityId) {
    String lockKey = "seckill:lock:" + activityId;
    RLock lock = redissonClient.getLock(lockKey);
    
    try {
        if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
            // 执行秒杀逻辑
            // ...
        }
    } finally {
        lock.unlock();
    }
}
```

### 2. 自定义限流注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default "";
    int time() default 60;
    int count() default 100;
}
```

### 3. 二维码支付

- 生成支付二维码
- 实时轮询支付状态
- 支付成功自动更新订单

## 📊 系统截图

（可以在这里添加系统截图）

## 🔧 配置说明

### 数据库配置

修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/seckill_db
    username: root
    password: your_password
```

### Redis 配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
```

### RabbitMQ 配置

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 📈 性能优化

- **Redis 缓存预热**：系统启动时预加载秒杀活动数据
- **分布式锁**：防止库存超卖
- **接口限流**：每秒最多处理 100 个请求
- **乐观锁**：数据库层面防止并发问题
- **异步处理**：使用 RabbitMQ 异步创建订单

## 🛠️ 待优化项

- [ ] 接入 Spring Cloud Gateway 网关
- [ ] 添加 Nacos 服务注册与发现
- [ ] 实现配置中心
- [ ] 添加分布式链路追踪
- [ ] 优化前端性能（代码分割、懒加载）
- [ ] 添加单元测试和集成测试
- [ ] 对接真实支付平台

## 📝 开发文档

- [快速启动指南](QUICKSTART.md)
- [部署文档](DEPLOY.md)
- [面试准备](面试准备-技术问答.md)
- [二维码支付功能说明](二维码支付功能说明.md)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 [MIT](LICENSE) 许可证

## 👨‍💻 作者

- GitHub: [@your-username](https://github.com/your-username)
- Email: your-email@example.com

## ⭐ Star History

如果这个项目对您有帮助，请给个 Star ⭐ 支持一下！

---

**注意**：本项目仅供学习交流使用，不建议直接用于生产环境。

