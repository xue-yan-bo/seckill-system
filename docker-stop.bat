@echo off
chcp 65001 >nul
echo ========================================
echo   秒杀系统 - 停止所有Docker服务
echo ========================================
echo.

docker-compose down

echo.
echo ✅ 所有服务已停止
echo.
pause

