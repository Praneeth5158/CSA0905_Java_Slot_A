@echo off
echo ====================================================================
echo Launching Smart Campus EV Charging Control Center
echo ====================================================================

java -cp "bin;lib/*" com.campus.ev.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited or encountered an issue.
    pause
)
