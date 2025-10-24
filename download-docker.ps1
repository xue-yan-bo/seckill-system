# PowerShell脚本：自动下载Docker Desktop到D:\developp

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Docker Desktop 自动下载脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 目标文件夹
$targetDir = "D:\developp"
$outputFile = "$targetDir\DockerDesktopInstaller.exe"

# 创建目录
if (!(Test-Path $targetDir)) {
    Write-Host "创建文件夹: $targetDir" -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
}

# Docker Desktop下载地址（官方）
$dockerUrl = "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"

Write-Host "下载地址: $dockerUrl" -ForegroundColor Green
Write-Host "保存位置: $outputFile" -ForegroundColor Green
Write-Host ""
Write-Host "开始下载... 这可能需要几分钟" -ForegroundColor Yellow
Write-Host ""

try {
    # 下载文件
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $dockerUrl -OutFile $outputFile -UseBasicParsing
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✅ 下载完成！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "文件位置: $outputFile" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "接下来请执行以下步骤：" -ForegroundColor Yellow
    Write-Host "1. 双击运行安装程序: $outputFile" -ForegroundColor White
    Write-Host "2. 勾选 'Use WSL 2 instead of Hyper-V'" -ForegroundColor White
    Write-Host "3. 点击 'Ok' 开始安装" -ForegroundColor White
    Write-Host "4. 安装完成后重启电脑" -ForegroundColor White
    Write-Host "5. 启动Docker Desktop" -ForegroundColor White
    Write-Host "6. 运行 check-docker.bat 检查安装" -ForegroundColor White
    Write-Host ""
    
    # 询问是否立即安装
    $install = Read-Host "是否立即运行安装程序？(Y/N)"
    if ($install -eq "Y" -or $install -eq "y") {
        Start-Process $outputFile
    }
    
} catch {
    Write-Host ""
    Write-Host "❌ 下载失败: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "请尝试以下方法：" -ForegroundColor Yellow
    Write-Host "1. 检查网络连接" -ForegroundColor White
    Write-Host "2. 使用VPN或代理" -ForegroundColor White
    Write-Host "3. 手动下载：" -ForegroundColor White
    Write-Host "   官网: https://www.docker.com/products/docker-desktop" -ForegroundColor Cyan
    Write-Host "   国内镜像: https://mirrors.aliyun.com/docker-toolbox/windows/docker-desktop/" -ForegroundColor Cyan
    Write-Host ""
}

Write-Host "按任意键退出..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

