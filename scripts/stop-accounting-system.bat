@echo off
title Stop Accounting System
color 0C
set "CATALINA_HOME=C:\xampp\tomcat"
set "JRE_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot"

echo Stopping Apache Tomcat...
if exist "%CATALINA_HOME%\bin\catalina.bat" (
    cmd /c "set CATALINA_HOME=%CATALINA_HOME%&& set JRE_HOME=%JRE_HOME%&& %CATALINA_HOME%\bin\catalina.bat stop" >nul 2>&1
)
powershell -Command "$p = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue; if ($p) { Stop-Process -Id $p.OwningProcess -Force -ErrorAction SilentlyContinue; Write-Host '[OK] Tomcat process stopped.' } else { Write-Host '[OK] Port 8080 is clear.' }"
echo.
echo Apache Tomcat has been stopped.
timeout /t 2 /nobreak >nul
