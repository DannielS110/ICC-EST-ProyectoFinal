@echo off
echo ========================================
echo Compilando Resolvedor de Laberintos
echo ========================================
echo.

if not exist "bin" mkdir bin

echo Limpiando compilacion anterior...
rmdir /s /q bin 2>nul
mkdir bin

echo.
echo Compilando modelo...
javac -d bin -cp src src/model/*.java

echo Compilando controladores...
javac -d bin -cp "src;bin" src/controllers/*.java

echo Compilando data...
javac -d bin -cp "src;bin" src/data/*.java

echo Compilando vistas...
javac -d bin -cp "src;bin" src/views/*.java

echo Compilando App...
javac -d bin -cp "src;bin" App.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo COMPILACION EXITOSA!
    echo ========================================
    echo.
    echo Ejecuta: ejecutar_sin_libs.bat
) else (
    echo.
    echo ========================================
    echo ERROR EN LA COMPILACION
    echo ========================================
)

pause