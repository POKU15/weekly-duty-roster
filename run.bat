@echo off
REM Dunkwa FPU Weekly Duty Roster - Launch Script for Windows

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not found in PATH
    echo Please install Java 17 or later
    pause
    exit /b 1
)

REM Get Java version
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VERSION=%%i

echo Found Java: %JAVA_VERSION%

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0

REM Run the application
set JAR_FILE=%SCRIPT_DIR%build\weekly-duty-roster.jar

if not exist "%JAR_FILE%" (
    echo Error: %JAR_FILE% not found
    echo Please run: gradle build
    pause
    exit /b 1
)

echo Starting Dunkwa FPU Weekly Duty Roster...
java -jar "%JAR_FILE%"
pause
