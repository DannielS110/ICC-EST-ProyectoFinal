@echo off
echo ========================================
echo Compilando Resolvedor de Laberintos
echo ========================================
echo.

if not exist "bin" mkdir bin

echo Compilando archivos Java...
javac -d bin -cp src src/model/*.java src/views/*.java src/controllers/*.java src/data/*.java App.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo COMPILACION EXITOSA!
    echo ========================================
    echo.
    echo Ejecuta: ejecutar.bat
) else (
    echo.
    echo ========================================
    echo ERROR EN LA COMPILACION
    echo ========================================
)

pause