@echo off
echo Starting Novel Reading App API...

REM Check if MongoDB is running (Windows)
sc query MongoDB >nul 2>&1
if %errorlevel% neq 0 (
    echo MongoDB service is not running. Please start MongoDB first.
    echo Run: net start MongoDB
    pause
    exit /b 1
)

REM Set active profile
set SPRING_PROFILES_ACTIVE=dev

REM Run the application
gradlew.bat bootRun
