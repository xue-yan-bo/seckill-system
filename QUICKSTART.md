# 快速启动指南

本文档帮助您快速启动电商秒杀系统。

## 前置条件

确保您的计算机已安装以下软件：

- ✅ JDK 1.8+
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ Redis 5.0+
- ✅ RabbitMQ 3.8+
- ✅ Node.js 16+

## 快速启动（5分钟）

### 步骤1：启动基础服务

#### Windows系统

```powershell
# 启动MySQL（已安装为服务）
net start MySQL80

# 启动Redis
# 打开Redis安装目录，双击 redis-server.exe

# 启动RabbitMQ
# 打开RabbitMQ安装目录\sbin，双击 rabbitmq-server.bat
```

#### Linux/Mac系统

```bash
# 启动MySQL
sudo systemctl start mysql

# 启动Redis
sudo systemctl start redis

# 启动RabbitMQ
sudo systemctl start rabbitmq-server
```

### 步骤2：初始化数据库

```bash
# 连接到MySQL
mysql -u root -p

# 执行以下命令
source database/init.sql

# 或者直接执行
mysql -u root -p < database/init.sql
```

### 步骤3：配置后端

修改各服务的 `application.yml` 文件（如果需要）：

```yaml
spring:
  datasource:
    username: root
    password: root  # 改为你的MySQL密码
  
  redis:
    password:       # 如果Redis设置了密码，填写在这里
  
  rabbitmq:
    username: guest
    password: guest
```

### 步骤4：启动后端服务

打开4个终端窗口，分别执行：

#### 终端1 - 用户服务

```bash
cd backend/seckill-user
mvn spring-boot:run
```

#### 终端2 - 商品服务

```bash
cd backend/seckill-product
mvn spring-boot:run
```

#### 终端3 - 秒杀服务

```bash
cd backend/seckill-seckill
mvn spring-boot:run
```

#### 终端4 - 订单服务

```bash
cd backend/seckill-order
mvn spring-boot:run
```

等待所有服务启动完成（看到"启动成功"提示）。

### 步骤5：启动前端

打开新的终端窗口：

```bash
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

### 步骤6：访问系统

在浏览器中打开：

```
http://localhost:3000
```

## 测试账号

使用以下账号登录系统：

| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin  | 123456 | 管理员账号 |
| user1  | 123456 | 普通用户1 |
| user2  | 123456 | 普通用户2 |

## 功能测试

### 1. 用户功能

- ✅ 注册新用户
- ✅ 登录系统
- ✅ 查看个人信息
- ✅ 退出登录

### 2. 商品功能

- ✅ 浏览商品列表
- ✅ 搜索商品
- ✅ 查看商品详情

### 3. 秒杀功能

- ✅ 查看秒杀活动列表
- ✅ 参与秒杀活动
- ✅ 查看秒杀结果

### 4. 订单功能

- ✅ 查看订单列表
- ✅ 支付订单
- ✅ 取消订单

## 常见问题

### Q1: 端口被占用怎么办？

**问题**：启动时报错 `Port 8081 is already in use`

**解决方案**：

Windows:
```powershell
# 查找占用端口的进程
netstat -ano | findstr :8081

# 结束进程
taskkill /PID <进程ID> /F
```

Linux/Mac:
```bash
# 查找占用端口的进程
lsof -i :8081

# 结束进程
kill -9 <进程ID>
```

### Q2: 数据库连接失败？

**问题**：`Cannot connect to database`

**解决方案**：
1. 检查MySQL服务是否启动
2. 检查用户名密码是否正确
3. 确认数据库 `seckill_db` 已创建
4. 检查MySQL端口是否为3306

### Q3: Redis连接失败？

**问题**：`Unable to connect to Redis`

**解决方案**：
1. 检查Redis服务是否启动
2. 测试连接：`redis-cli ping`（应返回PONG）
3. 检查Redis配置文件中的绑定IP和端口

### Q4: RabbitMQ连接失败？

**问题**：`Failed to connect to RabbitMQ`

**解决方案**：
1. 检查RabbitMQ服务是否启动
2. 访问管理界面：http://localhost:15672
3. 检查用户名密码是否正确
4. 确认虚拟主机（/）存在

### Q5: 前端页面空白？

**问题**：打开浏览器页面显示空白

**解决方案**：
1. 检查浏览器控制台是否有错误
2. 确认后端服务已全部启动
3. 清除浏览器缓存并刷新
4. 检查Vite开发服务器是否正常运行

### Q6: 秒杀失败？

**问题**：点击秒杀按钮提示失败

**解决方案**：
1. 确认已登录
2. 检查是否已参与过该秒杀活动
3. 查看后端日志排查具体原因
4. 确认Redis和RabbitMQ服务正常

## 服务端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 用户服务 | 8081 | 处理用户注册、登录等 |
| 商品服务 | 8082 | 处理商品查询、管理等 |
| 秒杀服务 | 8083 | 处理秒杀核心逻辑 |
| 订单服务 | 8084 | 处理订单创建、支付等 |
| 前端服务 | 3000 | Vue3开发服务器 |
| MySQL | 3306 | 数据库服务 |
| Redis | 6379 | 缓存服务 |
| RabbitMQ | 5672 | 消息队列服务 |
| RabbitMQ管理界面 | 15672 | RabbitMQ Web管理 |

## 停止服务

### 停止后端服务

在各个后端服务的终端窗口中按 `Ctrl + C`

### 停止前端服务

在前端终端窗口中按 `Ctrl + C`

### 停止基础服务

#### Windows

```powershell
# 停止MySQL
net stop MySQL80

# 停止Redis（关闭窗口）

# 停止RabbitMQ（关闭窗口）
```

#### Linux/Mac

```bash
# 停止MySQL
sudo systemctl stop mysql

# 停止Redis
sudo systemctl stop redis

# 停止RabbitMQ
sudo systemctl stop rabbitmq-server
```

## 开发模式 vs 生产模式

### 开发模式（当前）

- ✅ 快速启动
- ✅ 热重载
- ✅ 详细日志
- ❌ 性能较低
- ❌ 不适合生产

### 生产模式

参考 [DEPLOY.md](DEPLOY.md) 文档进行生产环境部署。

## 下一步

1. 📖 阅读 [README.md](README.md) 了解项目详情
2. 🚀 查看 [DEPLOY.md](DEPLOY.md) 学习生产部署
3. 🔧 修改代码，开始你的定制开发
4. 📊 集成监控工具，观察系统性能

## 获取帮助

遇到问题？

1. 查看项目文档
2. 检查服务日志
3. 提交GitHub Issue
4. 联系技术支持

## 许可证

MIT License

---

祝您使用愉快！🎉




