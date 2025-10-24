@echo off
chcp 65001 >nul
echo ========================================
echo   秒杀系统 - Docker一键启动
echo ========================================
echo.

echo [1/3] 检查Docker是否运行中...
docker version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker未启动或未安装！
    echo.
    echo 请先安装Docker Desktop，并启动它。
    echo 安装包位置: D:\developp\DockerDesktopInstaller.exe
    echo 下载地址: https://www.docker.com/products/docker-desktop
    echo.
    pause
    exit /b 1
)
echo ✅ Docker正在运行

echo.
echo [2/3] 停止旧容器（如果有）...
docker-compose down 2>nul

echo.
echo [3/3] 启动所有服务...
echo 这可能需要几分钟，请耐心等待...
echo.
docker-compose up -d --build

if errorlevel 1 (
    echo.
    echo ❌ 启动失败！请检查错误信息。
    pause
    exit /b 1
)

echo.
echo ========================================
echo   🎉 启动成功！
echo ========================================
echo.
echo 📱 前端地址:    http://localhost:8080
echo 🔧 API地址:     http://localhost:3000
echo 🗄️  MySQL:       localhost:3306 (root/root)
echo 📦 Redis:        localhost:6379
echo 🐰 RabbitMQ:     localhost:15672 (guest/guest)
echo.
echo 💡 查看日志:     docker-compose logs -f
echo 🛑 停止服务:     docker-stop.bat
echo ========================================
echo.
pause

