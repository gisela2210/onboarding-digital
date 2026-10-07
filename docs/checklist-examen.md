# Checklist de requisitos del examen

- [x] Procedimiento documentado en Markdown (`README.md`, `docs/flujo.md`).
- [x] Diagrama de flujo en Mermaid (`docs/flujo.md`).
- [x] Creación de cuenta e inicio de sesión.
- [x] Interfaz React responsive y de fácil manejo.
- [x] Tres grupos post-registro: información personal, dirección y documentos.
- [x] Endpoints separados por responsabilidad: auth, profile, account, documents, audit, location y recovery.
- [x] Datos sensibles y archivos cifrados con AES-256-GCM; contraseñas con BCrypt.
- [x] Home con `Hola, {nombre_usuario}`.
- [x] Edición de onboarding y perfil.
- [x] Token JWT HS256 con expiración.
- [x] Auditoría consultable: user id, acción, IP y timestamp.
- [x] Manejo centralizado de excepciones y persistencia transaccional de JPA/PostgreSQL.
- [x] Pruebas unitarias y JaCoCo configurado para generar reporte de cobertura.
- [x] Borrado lógico de usuarios y documentos.
- [x] Dockerfiles + Docker Compose.
- [x] Trigger `trg_user_registration` para contador de registros.
- [x] Stored procedure `soft_delete_user`.
- [x] Script PostgreSQL (`database/database.sql`).
- [x] DER (`docs/DER.md`).
- [x] Frontend React; backend Java 17/Spring Boot; PostgreSQL 16/JPA.

## Cobertura
Ejecutar `mvn test` en `backend/` y abrir `backend/target/site/jacoco/index.html`. El requisito contractual es **>= 60%**; el porcentaje debe verificarse con el reporte generado en la máquina de entrega antes de presentar el proyecto.

## Fuente del catálogo postal
La estructura del catálogo sigue SEPOMEX/Correos de México. El CP 91810 se incluye como respaldo local de demostración; el resto se consulta mediante un endpoint público sin credenciales. Si la empresa exige operación 100% offline nacional, debe importarse el catálogo oficial completo a PostgreSQL antes del despliegue.
