@echo off
title Stop Accounting System
color 0C
set "CATALINA_HOME=C:\xampp\tomcat"
set "JRE_HOME=C:\Users\0xp47\.jdks\jdk-25.0.5"

echo Stopping Apache Tomcat...
if exist "%CATALINA_HOME%\bin\catalina.bat" (
    cmd /c "set CATALINA_HOME=%CATALINA_HOME%&& set JRE_HOME=%JRE_HOME%&& %CATALINA_HOME%\bin\catalina.bat stop" >nul 2>&1
)
powershell -Command "foreach ($pt in 8888, 8080) { $p = Get-NetTCPConnection -LocalPort $pt -ErrorAction SilentlyContinue; if ($p) { Stop-Process -Id $p.OwningProcess -Force -ErrorAction SilentlyContinue; Write-Host \"[OK] Process on port $pt stopped.\" } }"
echo.
echo Apache Tomcat has been stopped.
timeout /t 2 /nobreak >nul
