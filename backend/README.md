# ⚙️ 后端项目（Backend）

> Spring Boot 多模块微服务架构

## 📂 这是后端代码目录

**技术栈**：
- Spring Boot 2.7.14 - 核心框架
- MyBatis Plus - ORM框架
- MySQL - 关系型数据库
- Redis - 缓存数据库
- RabbitMQ - 消息队列
- Redisson - 分布式锁
- JWT - Token认证

## 🏗️ 模块说明

| 模块 | 端口 | 功能 |
|------|------|------|
| seckill-common | - | 公共工具类 |
| seckill-model | - | 实体类定义 |
| seckill-user | 8081 | 用户服务 |
| seckill-product | 8082 | 商品服务 |
| seckill-seckill | 8083 | 秒杀服务 |
| seckill-order | 8084 | 订单服务 |

## 🚀 启动方式

### 方式一：Maven命令

```bash
# 进入任意服务目录
cd seckill-user

# 启动服务
mvn spring-boot:run
```

### 方式二：IDEA运行

1. 在IDEA中打开 `backend/pom.xml`
2. 右键任意 `*Application.java`
3. 选择 "Run '*Application'"

## 📋 启动前准备

1. ✅ 安装MySQL并运行
2. ✅ 安装Redis并运行
3. ✅ 安装RabbitMQ并运行
4. ✅ 执行 `database/init.sql` 初始化数据库

## 🎯 核心功能

- ✅ JWT Token认证
- ✅ 分布式锁（防止超卖）
- ✅ 接口限流（防止刷单）
- ✅ 乐观锁（并发控制）
- ✅ Redis缓存（性能优化）
- ✅ RabbitMQ异步（订单处理）
- ✅ AOP日志监控
- ✅ 全局异常处理

## 🔧 开发工具

推荐使用：**IntelliJ IDEA**

## 📝 注意事项

1. 确保JDK版本为17
2. 修改 `application.yml` 中的数据库配置
3. 各服务端口不能冲突

