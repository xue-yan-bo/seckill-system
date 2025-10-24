# 部署文档

本文档详细说明如何将电商秒杀系统部署到生产环境。

## 目录

- [环境准备](#环境准备)
- [数据库部署](#数据库部署)
- [Redis部署](#redis部署)
- [RabbitMQ部署](#rabbitmq部署)
- [后端部署](#后端部署)
- [前端部署](#前端部署)
- [Nginx配置](#nginx配置)
- [Docker部署](#docker部署)
- [监控与日志](#监控与日志)

## 环境准备

### 服务器要求

- **操作系统**：Ubuntu 20.04+ / CentOS 7+ / Windows Server 2019+
- **CPU**：4核心以上
- **内存**：8GB以上
- **磁盘**：50GB以上

### 软件版本

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.9+
- Node.js 16+
- Nginx 1.18+

## 数据库部署

### 1. 安装MySQL

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# CentOS
sudo yum install mysql-server

# 启动MySQL服务
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 2. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/database/init.sql
```

### 3. 配置优化

编辑 `/etc/mysql/my.cnf`：

```ini
[mysqld]
# 最大连接数
max_connections = 500

# InnoDB缓冲池大小
innodb_buffer_pool_size = 2G

# 日志文件大小
innodb_log_file_size = 256M

# 字符集
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

重启MySQL：

```bash
sudo systemctl restart mysql
```

## Redis部署

### 1. 安装Redis

```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS
sudo yum install redis

# 启动Redis服务
sudo systemctl start redis
sudo systemctl enable redis
```

### 2. 配置优化

编辑 `/etc/redis/redis.conf`：

```conf
# 绑定IP（生产环境改为具体IP）
bind 127.0.0.1

# 密码设置
requirepass your_strong_password

# 最大内存
maxmemory 2gb

# 内存淘汰策略
maxmemory-policy allkeys-lru

# 持久化
save 900 1
save 300 10
save 60 10000
```

重启Redis：

```bash
sudo systemctl restart redis
```

## RabbitMQ部署

### 1. 安装RabbitMQ

```bash
# Ubuntu/Debian
sudo apt install rabbitmq-server

# CentOS
sudo yum install rabbitmq-server

# 启动RabbitMQ服务
sudo systemctl start rabbitmq-server
sudo systemctl enable rabbitmq-server
```

### 2. 启用管理插件

```bash
sudo rabbitmq-plugins enable rabbitmq_management
```

访问管理界面：http://localhost:15672
默认用户名/密码：guest/guest

### 3. 创建用户

```bash
# 创建管理员用户
sudo rabbitmqctl add_user admin password
sudo rabbitmqctl set_user_tags admin administrator
sudo rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
```

## 后端部署

### 方式一：手动部署

#### 1. 打包项目

```bash
cd backend
mvn clean package -DskipTests
```

#### 2. 上传JAR文件

将各模块的JAR文件上传到服务器：

```bash
# 示例
scp target/seckill-user-1.0.0.jar user@server:/opt/seckill/
```

#### 3. 创建启动脚本

创建 `/opt/seckill/start-user.sh`：

```bash
#!/bin/bash
nohup java -jar \
  -Xms512m \
  -Xmx1024m \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/seckill/logs/ \
  /opt/seckill/seckill-user-1.0.0.jar \
  --spring.profiles.active=prod \
  > /opt/seckill/logs/user.log 2>&1 &

echo $! > /opt/seckill/user.pid
```

#### 4. 创建停止脚本

创建 `/opt/seckill/stop-user.sh`：

```bash
#!/bin/bash
PID=$(cat /opt/seckill/user.pid)
kill -9 $PID
rm /opt/seckill/user.pid
```

#### 5. 设置执行权限并启动

```bash
chmod +x /opt/seckill/start-user.sh
chmod +x /opt/seckill/stop-user.sh

# 启动服务
./start-user.sh
```

对其他服务重复以上步骤。

### 方式二：使用systemd

创建 `/etc/systemd/system/seckill-user.service`：

```ini
[Unit]
Description=Seckill User Service
After=network.target

[Service]
Type=simple
User=seckill
WorkingDirectory=/opt/seckill
ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m /opt/seckill/seckill-user-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start seckill-user
sudo systemctl enable seckill-user
```

## 前端部署

### 1. 构建项目

```bash
cd frontend
npm install
npm run build
```

### 2. 部署到服务器

```bash
# 将dist目录上传到服务器
scp -r dist/* user@server:/var/www/seckill/
```

## Nginx配置

### 1. 安装Nginx

```bash
# Ubuntu/Debian
sudo apt install nginx

# CentOS
sudo yum install nginx
```

### 2. 配置文件

创建 `/etc/nginx/sites-available/seckill`：

```nginx
# 后端服务负载均衡
upstream backend_user {
    server 127.0.0.1:8081;
}

upstream backend_product {
    server 127.0.0.1:8082;
}

upstream backend_seckill {
    server 127.0.0.1:8083;
}

upstream backend_order {
    server 127.0.0.1:8084;
}

server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/seckill;
        try_files $uri $uri/ /index.html;
        index index.html;
    }

    # 用户服务API代理
    location /api/user/ {
        proxy_pass http://backend_user/api/user/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 商品服务API代理
    location /api/product/ {
        proxy_pass http://backend_product/api/product/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 秒杀服务API代理
    location /api/seckill/ {
        proxy_pass http://backend_seckill/api/seckill/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 订单服务API代理
    location /api/order/ {
        proxy_pass http://backend_order/api/order/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 限制上传文件大小
    client_max_body_size 10M;

    # 压缩配置
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

### 3. 启用配置并重启

```bash
sudo ln -s /etc/nginx/sites-available/seckill /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## Docker部署

### 1. 创建Dockerfile

#### 后端Dockerfile

创建 `backend/Dockerfile`：

```dockerfile
FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

#### 前端Dockerfile

创建 `frontend/Dockerfile`：

```dockerfile
FROM node:16-alpine as build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 2. 创建docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: seckill_db
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

  user-service:
    build: ./backend/seckill-user
    ports:
      - "8081:8081"
    depends_on:
      - mysql
      - redis

  product-service:
    build: ./backend/seckill-product
    ports:
      - "8082:8082"
    depends_on:
      - mysql
      - redis

  seckill-service:
    build: ./backend/seckill-seckill
    ports:
      - "8083:8083"
    depends_on:
      - mysql
      - redis
      - rabbitmq

  order-service:
    build: ./backend/seckill-order
    ports:
      - "8084:8084"
    depends_on:
      - mysql
      - redis
      - rabbitmq

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - user-service
      - product-service
      - seckill-service
      - order-service

volumes:
  mysql-data:
  redis-data:
```

### 3. 启动服务

```bash
docker-compose up -d
```

## 监控与日志

### 1. 日志配置

在 `application.yml` 中配置日志：

```yaml
logging:
  level:
    com.seckill: info
  file:
    path: /opt/seckill/logs
    name: ${spring.application.name}.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
```

### 2. 日志轮转

创建 `/etc/logrotate.d/seckill`：

```conf
/opt/seckill/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    notifempty
    create 0640 seckill seckill
    sharedscripts
}
```

### 3. 监控工具

推荐使用：
- **Spring Boot Admin** - 应用监控
- **Prometheus + Grafana** - 系统监控
- **ELK Stack** - 日志分析

## 安全建议

1. **修改默认密码**：MySQL、Redis、RabbitMQ等
2. **使用防火墙**：只开放必要端口
3. **启用SSL/TLS**：使用HTTPS
4. **定期备份**：数据库和重要文件
5. **限制访问**：IP白名单
6. **更新依赖**：定期更新安全补丁

## 性能优化

1. **JVM参数调优**：根据服务器配置调整
2. **数据库索引**：合理创建索引
3. **Redis缓存**：热点数据缓存
4. **连接池配置**：优化连接池大小
5. **Nginx缓存**：静态资源缓存

## 故障排查

### 服务无法启动

```bash
# 查看日志
tail -f /opt/seckill/logs/user.log

# 查看端口占用
netstat -tunlp | grep 8081

# 查看服务状态
systemctl status seckill-user
```

### 数据库连接失败

```bash
# 检查MySQL状态
systemctl status mysql

# 测试连接
mysql -h localhost -u root -p

# 查看错误日志
tail -f /var/log/mysql/error.log
```

### Redis连接失败

```bash
# 检查Redis状态
systemctl status redis

# 测试连接
redis-cli ping

# 查看日志
tail -f /var/log/redis/redis-server.log
```

## 维护计划

1. **每日**：检查日志、监控告警
2. **每周**：备份数据库、清理日志
3. **每月**：性能分析、容量规划
4. **每季度**：安全审计、依赖更新

## 联系支持

如有问题，请联系：
- 技术支持：support@example.com
- 紧急电话：1234567890




