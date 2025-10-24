@echo off
chcp 65001 >nul
echo ========================================
echo   检查Docker环境
echo ========================================
echo.

echo [检查1] Docker是否已安装...
where docker >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker未安装
    echo.
    echo 请按照以下步骤安装Docker Desktop：
    echo 1. 创建文件夹: D:\developp
    echo 2. 下载Docker Desktop到该文件夹
    echo 3. 运行安装程序
    echo 4. 重启电脑
    echo.
    echo 下载地址: https://www.docker.com/products/docker-desktop
    echo 或使用国内镜像: https://mirrors.aliyun.com/docker-toolbox/windows/docker-desktop/
    echo.
    pause
    exit /b 1
) else (
    echo ✅ Docker已安装
)

echo.
echo [检查2] Docker服务是否运行中...
docker version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker服务未运行
    echo.
    echo 请打开Docker Desktop应用
    echo 等待状态栏显示"Docker Desktop is running"
    echo.
    pause
    exit /b 1
) else (
    echo ✅ Docker服务正在运行
)

echo.
echo [检查3] Docker Compose是否可用...
docker-compose version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose不可用
    pause
    exit /b 1
) else (
    echo ✅ Docker Compose可用
)

echo.
echo ========================================
echo   ✅ 环境检查通过！
echo ========================================
echo.
echo Docker版本信息：
docker version
echo.
echo Docker Compose版本信息：
docker-compose version
echo.
echo 现在可以运行 docker-start.bat 启动项目了！
echo.
pause

