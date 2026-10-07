$ErrorActionPreference = "Stop"
Set-Location "$PSScriptRoot\backend"
Write-Host "Ejecutando pruebas unitarias y verificacion JaCoCo >= 60%..." -ForegroundColor Cyan
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean verify
if ($LASTEXITCODE -ne 0) { throw "Las pruebas o la cobertura minima fallaron." }
Write-Host "OK: pruebas aprobadas y cobertura minima de 60% verificada por JaCoCo." -ForegroundColor Green
Write-Host "Abriendo reporte: backend\target\site\jacoco\index.html"
Start-Process ".\target\site\jacoco\index.html"
