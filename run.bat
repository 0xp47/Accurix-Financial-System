@echo off
setlocal enabledelayedexpansion
title Accurix Financial Launcher — Double-Entry Bookkeeping [Java 25]

:: =====================================================================
:: Global Environment Configuration
:: =====================================================================
set "PROJECT_DIR=%~dp0"
set "XAMPP_DIR=C:\xampp"
set "CATALINA_HOME=%XAMPP_DIR%\tomcat"
set "CATALINA_BASE=%XAMPP_DIR%\tomcat"
set "TOMCAT_PORT=8888"

:: 1. Detect Java 25 Runtime
:: Priority: Check dedicated Java 25 installations first so they override older system JAVA_HOME (e.g. Java 17)
set "JAVA_25_HOME="
if exist "%USERPROFILE%\.jdks\jdk-25.0.5\bin\java.exe" (
    set "JAVA_25_HOME=%USERPROFILE%\.jdks\jdk-25.0.5"
) else if exist "C:\Users\0xp47\.jdks\jdk-25.0.5\bin\java.exe" (
    set "JAVA_25_HOME=C:\Users\0xp47\.jdks\jdk-25.0.5"
) else if exist "C:\Program Files\Java\jdk-25\bin\java.exe" (
    set "JAVA_25_HOME=C:\Program Files\Java\jdk-25"
)

if defined JAVA_25_HOME (
    set "JAVA_HOME=%JAVA_25_HOME%"
    set "JRE_HOME=%JAVA_25_HOME%"
    set "JAVA_BIN=%JAVA_25_HOME%\bin\java.exe"
    set "PATH=%JAVA_25_HOME%\bin;%PATH%"
) else if defined JAVA_HOME (
    set "JAVA_BIN=%JAVA_HOME%\bin\java.exe"
    set "JRE_HOME=%JAVA_HOME%"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else (
    set "JAVA_BIN=java"
)

:: 2. Detect Maven
set "MVN_CMD="
if exist "%PROJECT_DIR%.tools\apache-maven-3.9.6\bin\mvn.cmd" (
    set "MVN_CMD=%PROJECT_DIR%.tools\apache-maven-3.9.6\bin\mvn.cmd"
) else (
    set "MVN_CMD=mvn"
)

:MENU
cls
echo ======================================================================
echo             ACCURIX FINANCIAL -- DOUBLE-ENTRY LEDGER SUITE
echo                 Philippine GAAP / IFRS Compliant [PHP]
echo ======================================================================
echo.
echo   Select which system you would like to run:
echo.
echo   [1] Native Desktop Java GUI (Accurix Desktop Application)
echo   [2] Web-Based Application (Accurix Web on Apache Tomcat)
echo   [3] Launch BOTH Systems Concurrently (Desktop GUI + Web System)
echo   [4] Stop Web Application (Shut down Apache Tomcat server)
echo   [5] Exit
echo.
echo ======================================================================
choice /c 12345 /m "Enter choice [1-5]: "

if errorlevel 5 goto EXIT_APP
if errorlevel 4 goto STOP_WEB
if errorlevel 3 goto RUN_BOTH
if errorlevel 2 goto RUN_WEB
if errorlevel 1 goto RUN_GUI
goto MENU

:CHECK_PREREQS
echo.
echo [*] Checking Java 25 runtime...
"%JAVA_BIN%" -version 2>&1 | findstr "25." >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java 25 was not found!
    echo [*] Currently active Java runtime:
    "%JAVA_BIN%" -version
    echo.
    echo [!] Accurix Financial requires Java 25 LTS.
    echo [!] Please extract or install Java 25 to: %USERPROFILE%\.jdks\jdk-25.0.5
    pause
    goto MENU
)
for /f "tokens=*" %%v in ('"%JAVA_BIN%" -version 2^>^&1') do (
    echo [OK] Java 25 detected: %%v
    goto :done_java_check
)
:done_java_check

echo [*] Checking MySQL connection on port 3306...
powershell -Command "$t = New-Object Net.Sockets.TcpClient; try { $t.Connect('127.0.0.1', 3306); Write-Host '[OK] MySQL is active and listening on port 3306.'; $t.Close(); exit 0 } catch { exit 1 }"
if errorlevel 1 (
    echo [WARN] MySQL is not responding on port 3306.
    if exist "%XAMPP_DIR%\mysql\bin\mysqld.exe" (
        echo [*] Auto-starting XAMPP MySQL server...
        if exist "%XAMPP_DIR%\mysql\data\*.pid" del /F /Q "%XAMPP_DIR%\mysql\data\*.pid" >nul 2>&1
        start "MySQL Daemon" /B "%XAMPP_DIR%\mysql\bin\mysqld.exe" --defaults-file="%XAMPP_DIR%\mysql\bin\my.ini" --standalone
        timeout /t 3 /nobreak >nul
        powershell -Command "$t = New-Object Net.Sockets.TcpClient; try { $t.Connect('127.0.0.1', 3306); Write-Host '[OK] MySQL is now active on port 3306.'; $t.Close(); exit 0 } catch { exit 1 }"
        if errorlevel 1 (
            echo [WARN] Could not auto-start MySQL. Please ensure MySQL is started in XAMPP Control Panel.
        )
    ) else (
        echo [WARN] Please make sure MySQL is started in XAMPP.
    )
)
goto :eof

:DEPLOY_WAR
echo [*] Checking Web Application archive (WAR)...
if not exist "%PROJECT_DIR%target\accounting-system.war" (
    echo [*] Building accounting-system.war archive via Maven...
    call "%MVN_CMD%" package -DskipTests -q
)
if exist "%PROJECT_DIR%target\accounting-system.war" (
    if exist "%CATALINA_HOME%\webapps" (
        echo [*] Synchronizing accounting.war to Tomcat webapps...
        copy /Y "%PROJECT_DIR%target\accounting-system.war" "%CATALINA_HOME%\webapps\accounting.war" >nul
        echo [OK] Deployed accounting.war to Tomcat.
    )
)
goto :eof

:START_TOMCAT
call :DEPLOY_WAR
:: Ensure Tomcat HTTP connector is set to port 8888 (avoids Windows Hyper-V/NAT excluded port range 8044-8143)
if exist "%CATALINA_HOME%\conf\server.xml" (
    powershell -Command "$f='%CATALINA_HOME%\conf\server.xml'; $t=[IO.File]::ReadAllText($f); if ($t -match 'port=""8080""') { [IO.File]::WriteAllText($f, ($t -replace 'port=""8080""','port=""8888""')) }" >nul 2>&1
)
echo [*] Starting Apache Tomcat on port %TOMCAT_PORT%...
start "Apache Tomcat (Accounting Web System)" cmd /c "set CATALINA_HOME=%CATALINA_HOME%&& set JRE_HOME=%JRE_HOME%&& %CATALINA_HOME%\bin\catalina.bat run"
echo [*] Waiting for server initialization...
ping 127.0.0.1 -n 4 >nul
echo [*] Opening browser to http://localhost:%TOMCAT_PORT%/accounting ...
start http://localhost:%TOMCAT_PORT%/accounting
goto :eof

:RUN_GUI
call :CHECK_PREREQS
echo.
echo [*] Starting Accurix Desktop GUI (Vercel Geist Light Theme)...
echo.
call "%MVN_CMD%" compile exec:java -q
if errorlevel 1 (
    echo.
    echo [ERROR] Desktop GUI closed with an error.
    pause
)
goto MENU

:RUN_WEB
call :CHECK_PREREQS
call :START_TOMCAT
echo.
echo [OK] Accurix Web Application is running at http://localhost:%TOMCAT_PORT%/accounting
echo.
echo Press any key to return to main menu...
pause >nul
goto MENU

:RUN_BOTH
call :CHECK_PREREQS
echo.
echo ======================================================================
echo           LAUNCHING BOTH SYSTEMS (DESKTOP GUI + WEB SYSTEM)
echo ======================================================================
echo.
call :START_TOMCAT
echo.
echo [*] Accurix Web Application is running at http://localhost:%TOMCAT_PORT%/accounting
echo [*] Now launching Accurix Desktop Java GUI...
echo.
call "%MVN_CMD%" compile exec:java -q
echo.
echo [INFO] Desktop GUI session ended.
choice /c YN /m "Do you want to stop the Tomcat Web server as well? [Y/N]: "
if errorlevel 2 goto MENU
if errorlevel 1 (
    call :STOP_TOMCAT_INTERNAL
)
goto MENU

:STOP_WEB
echo.
call :STOP_TOMCAT_INTERNAL
echo.
echo Press any key to return to main menu...
pause >nul
goto MENU

:STOP_TOMCAT_INTERNAL
echo [*] Stopping Apache Tomcat server...
if exist "%CATALINA_HOME%\bin\catalina.bat" (
    cmd /c "set CATALINA_HOME=%CATALINA_HOME%&& set JRE_HOME=%JRE_HOME%&& %CATALINA_HOME%\bin\catalina.bat stop" >nul 2>&1
)
powershell -Command "$p = Get-NetTCPConnection -LocalPort %TOMCAT_PORT% -ErrorAction SilentlyContinue; if ($p) { Stop-Process -Id $p.OwningProcess -Force -ErrorAction SilentlyContinue; Write-Host '[OK] Tomcat process stopped.' } else { Write-Host '[OK] Port %TOMCAT_PORT% is clear.' }"
echo [OK] Web server stopped.
goto :eof

:EXIT_APP
echo.
echo Exiting launcher. Have a productive day!
ping 127.0.0.1 -n 2 >nul
exit /b 0
