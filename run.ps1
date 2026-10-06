Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "      Iniciando Tetris JavaFX (MVC)" -ForegroundColor Yellow
Write-Host "==============================================" -ForegroundColor Cyan

if (-not $env:JAVA_HOME -or ($env:JAVA_HOME -like "*jre1.8*")) {
    $jbrPath = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\jbr"
    if (Test-Path "$jbrPath\bin\java.exe") {
        $env:JAVA_HOME = $jbrPath
        $env:Path = "$jbrPath\bin;" + $env:Path
        Write-Host "Configurado JAVA_HOME a: $env:JAVA_HOME" -ForegroundColor Green
    }
}

mvn javafx:run
