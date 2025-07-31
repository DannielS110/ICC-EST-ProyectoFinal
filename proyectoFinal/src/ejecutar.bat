@echo off
echo ========================================
echo    Resolvedor de Laberintos
echo    Proyecto Final - Estructura de Datos
echo ========================================
echo.

java -cp bin App

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error al ejecutar. Verifica que Java este instalado.
    pause
)