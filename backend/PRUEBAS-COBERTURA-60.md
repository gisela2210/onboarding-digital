# Evidencia de pruebas unitarias y cobertura

Desde `backend`:

```powershell
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean verify
start .\target\site\jacoco\index.html
```

JaCoCo está configurado para exigir **60% mínimo de cobertura de líneas** sobre la lógica de aplicación medida (controllers, services, DTO y manejo de excepciones). Se excluyen del cálculo las entidades JPA, interfaces Repository, clase de arranque, configuración de seguridad declarativa e inicializadores de datos, porque no contienen la lógica de negocio que cubre esta suite unitaria.

`mvn verify` debe finalizar con `BUILD SUCCESS` y `All coverage checks have been met`. El reporte HTML usa las mismas exclusiones que la regla de verificación para que el porcentaje visible y el porcentaje comprobado correspondan al mismo alcance.
