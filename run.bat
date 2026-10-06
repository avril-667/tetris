@echo off
setlocal

echo ==============================================
echo       Iniciando Tetris JavaFX (MVC)
echo ==============================================

:: Verificar si JAVA_HOME esta configurado y es >= 17; si no, buscar JDK en el sistema
if defined JAVA_HOME (
    goto CHECK_MVN
)

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\jbr\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\jbr"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo Configurado JAVA_HOME a: %JAVA_HOME%
)

:CHECK_MVN
:: Ejecutar aplicacion con Maven y JavaFX
call mvn javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Ocurrio un error al ejecutar el juego.
    echo Asegurate de tener Java 17+ y Maven configurados.
    pause
)

endlocal
