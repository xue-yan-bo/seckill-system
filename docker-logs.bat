@echo off
chcp 65001 >nul
echo ========================================
echo   秒杀系统 - 查看服务日志
echo ========================================
echo.
echo 按 Ctrl+C 退出日志查看
echo.

docker-compose logs -f

