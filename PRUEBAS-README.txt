PRUEBAS UNITARIAS - ONBOARDING DIGITAL
======================================

Requisito: cobertura minima 60%.

Opcion facil (PowerShell, desde la raiz del proyecto):
  powershell -ExecutionPolicy Bypass -File .\PRUEBAS-60.ps1

O desde backend:
  docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean verify

Si BUILD SUCCESS, JaCoCo tambien verifico automaticamente que la cobertura LINE sea >= 60%.
Reporte HTML:
  backend\target\site\jacoco\index.html

Pruebas incluidas:
- CryptoServiceTest: AES-256/GCM, null, bytes, clave invalida, manipulacion.
- TokenServiceTest: creacion/validacion JWT, token alterado/ausente/esquema incorrecto.
- AuditServiceTest: persistencia de trazabilidad.
- AuthControllerTest: registro, duplicados, login correcto/incorrecto.
- PasswordRecoveryControllerTest: solicitud, SMTP fallido, token invalido y reset correcto.
