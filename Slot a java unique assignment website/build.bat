@echo off
echo ====================================================================
echo Building Smart Campus EV Charging Management System
echo ====================================================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac --release 8 -encoding UTF-8 -cp "lib/*;src" -d bin src\com\campus\ev\*.java src\com\campus\ev\config\*.java src\com\campus\ev\db\*.java src\com\campus\ev\model\*.java src\com\campus\ev\dao\*.java src\com\campus\ev\service\*.java src\com\campus\ev\util\*.java src\com\campus\ev\validation\*.java src\com\campus\ev\ui\*.java src\com\campus\ev\ui\components\*.java src\com\campus\ev\ui\dashboard\*.java src\com\campus\ev\ui\grid\*.java src\com\campus\ev\ui\reservation\*.java src\com\campus\ev\ui\session\*.java src\com\campus\ev\ui\management\*.java src\com\campus\ev\ui\reports\*.java src\com\campus\ev\ui\dialogs\*.java src\com\campus\ev\test\*.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation complete. All class files created in bin/
) else (
    echo [ERROR] Compilation failed.
)
