# 💾 数据库脚本

## 📂 这是数据库初始化目录

## 📄 文件说明

- `init.sql` - 数据库初始化脚本

## 📊 包含内容

1. ✅ 创建数据库 `seckill_db`
2. ✅ 创建表结构
   - tb_user（用户表）
   - tb_product（商品表）
   - tb_seckill_activity（秒杀活动表）
   - tb_order（订单表）
3. ✅ 插入测试数据

## 🚀 执行方式

### 方法一：命令行

```bash
mysql -u root -p < init.sql
```

### 方法二：DataGrip/Navicat

1. 打开数据库工具
2. 连接到MySQL
3. 打开 `init.sql` 文件
4. 执行脚本

### 方法三：MySQL Workbench

1. 打开MySQL Workbench
2. File → Open SQL Script
3. 选择 `init.sql`
4. 点击执行按钮 ⚡

## 📝 数据库配置

- 数据库名：`seckill_db`
- 字符集：`utf8mb4`
- 默认用户：root
- 默认密码：root（请修改）

## 🧪 测试数据

### 测试账号
- admin / 123456
- user1 / 123456
- user2 / 123456

### 测试商品
- iPhone 15 Pro
- 小米14
- 华为Mate 60 Pro
- MacBook Pro 16
- AirPods Pro 2

### 秒杀活动
- 3个进行中的秒杀活动

