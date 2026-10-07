# Sistema de Onboarding Digital

Examen práctico desarrollado con **Java 17 + Spring Boot 3**, **React** y **PostgreSQL 16**. Spring Data JPA se usa para persistencia y Docker Compose para el despliegue local.

## Ejecutar
Desde la raíz:
```powershell
docker compose down -v
docker compose up --build
```
Abrir `http://localhost:5173`.

## Funcionalidades
Registro/login, JWT, Home con saludo, información personal, dirección por código postal, documentos con previsualización, edición posterior, Mi perfil, cambio y recuperación de contraseña, auditoría y borrado lógico.

## Seguridad
- Contraseñas: BCrypt (nunca se descifran).
- Datos personales/dirección: AES-256-GCM.
- Contenido de documentos: AES-256-GCM.
- Sesión API: JWT HS256 con expiración.
- Las claves incluidas son solo para desarrollo; en un despliegue real deben suministrarse como secretos.

## Código postal
No requiere `COPOMEX_TOKEN`. El CP **91810** se resuelve localmente a **Veracruz de Ignacio de la Llave / Veracruz / Playa Linda**, por lo que la demostración funciona incluso sin Internet. Para otros CP el backend consulta una fuente pública SEPOMEX sin API key; si no hay conexión muestra un mensaje amigable y no expone detalles técnicos.

## Base de datos
PostgreSQL 16. El script completo está en `database/database.sql`. Incluye el trigger `trg_user_registration`, contador `registration_counter` y stored procedure `soft_delete_user`.

## Documentación
- `docs/flujo.md`: procedimiento y diagrama Mermaid.
- `docs/DER.md`: diagrama entidad-relación.
- `docs/checklist-examen.md`: correspondencia requisito por requisito.

## Pruebas
Desde `backend/` ejecutar `mvn test`. JaCoCo genera `target/site/jacoco/index.html`. Antes de entregar debe comprobarse que el reporte alcance el **60% mínimo exigido**; no debe inferirse el porcentaje solo por el número de pruebas.

## Recuperación de contraseña
Para demostración local el backend puede devolver el token temporal al frontend. En producción debe enviarse por correo y no exponerse en la respuesta HTTP.


## Funciones adicionales de esta entrega
- Login con mostrar/ocultar contraseña y **Recordar usuario** (solo conserva el correo, nunca la contraseña).
- Recuperación de contraseña por correo. En Docker se incluye **MailHog** para demostrar el envío sin credenciales externas: abrir `http://localhost:8025`. Para correo real, sustituir `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_AUTH` y `MAIL_STARTTLS`. El remitente se configura con `ADMIN_EMAIL`.
- Administrador inicial: `admin@onboarding.local` / `Admin2026!` (cambiar mediante variables `ADMIN_EMAIL` y `ADMIN_PASSWORD` antes de entregar).
- Panel administrador con usuarios, datos de contacto/dirección, documentos con previsualización y métricas.
- Catálogo postal: la tabla `postal_codes` funciona primero de forma local y el backend usa un respaldo público si el CP no está cargado. Para una entrega totalmente offline a nivel nacional, importar el TXT oficial de SEPOMEX a esa tabla.

### Probar recuperación de contraseña
1. Ejecutar `docker compose up --build`.
2. Registrar un usuario con un correo.
3. En Login seleccionar **¿Olvidaste tu contraseña?**.
4. Abrir `http://localhost:8025`, abrir el mensaje enviado por `admin@onboarding.local` y usar el enlace de recuperación.

### Panel administrador
Iniciar sesión con las credenciales configuradas en `ADMIN_EMAIL` / `ADMIN_PASSWORD`. En el entorno Docker incluido: `admin@onboarding.local` / `Admin2026!`.

## Cambios de la versión corregida

- **Administrador:** `ADMIN_EMAIL` siempre se inicializa/actualiza con rol `ADMIN`; al iniciar sesión React abre el panel administrativo. Usuario por defecto: `admin@onboarding.local`, contraseña `Admin2026!` (cámbialos con `.env`).
- **Recuperación por correo real:** se eliminó MailHog del flujo por defecto. Copia `.env.example` a `.env` y configura SMTP. Para Gmail utiliza una contraseña de aplicación. El enlace se envía al correo que el cliente registró.
- **Código postal:** si el CP se encuentra, Estado/Municipio/Colonias se completan automáticamente. Si no se encuentra o el servicio externo falla, el formulario habilita captura manual: selector de Estado y Municipio/colonia editables. Nunca bloquea el onboarding por no encontrar un CP.

### Configurar correo real (Windows / VS Code)

1. Copia `.env.example` como `.env` en la raíz.
2. Coloca el correo administrador en `ADMIN_EMAIL` y `MAIL_USERNAME`.
3. En `MAIL_PASSWORD` coloca la contraseña de aplicación de tu proveedor SMTP.
4. Ejecuta `docker compose down -v` y luego `docker compose up --build`.

Las credenciales SMTP no deben subirse a Git; `.env` está ignorado.

### Prueba rápida del administrador
Con la base limpia (`docker compose down -v`) el usuario indicado en `ADMIN_EMAIL` se fuerza a rol `ADMIN` al arrancar. Si usas los valores por defecto: `admin@onboarding.local` / `Admin2026!`. Al autenticar, el frontend lee `role=ADMIN` y muestra el Dashboard administrativo.

### Nota sobre SMTP
El sistema no puede enviar correo real sin credenciales de un servidor SMTP. La aplicación ya envía la recuperación **al correo registrado por el usuario**; `ADMIN_EMAIL`/`MAIL_USERNAME` actúan como remitente. Configura `.env` antes de levantar Docker.

## Correcciones de panel administrador y recuperación

El panel administrador consulta los usuarios reales almacenados en PostgreSQL y ahora incluye botón **Actualizar datos**, métricas de onboarding y actividad reciente. Los usuarios creados con el formulario se muestran en el panel después de actualizar.

La recuperación envía el enlace al correo registrado por el cliente. Para correo real copia `.env.example` a `.env` y configura SMTP. Con Gmail debes usar una **contraseña de aplicación**. El remitente SMTP debe ser la misma cuenta indicada en `MAIL_USERNAME`; `ADMIN_EMAIL` se usa como correo administrativo/reply-to.

Si el SMTP rechaza la autenticación, la API elimina el token de recuperación que acababa de generar y devuelve un mensaje claro, evitando dejar solicitudes activas que nunca fueron enviadas.


## Correcciones de esta entrega

- El dashboard ADMIN se actualiza automáticamente cada 5 segundos y también con **Actualizar datos**.
- La lectura administrativa tolera registros previos no cifrados y registros nuevos cifrados, evitando que un dato antiguo deje el panel vacío.
- La recuperación crea tokens explícitamente activos (`used=false`), con expiración de 20 minutos.
- El correo de recuperación se envía al correo registrado del cliente. El remitente SMTP se configura únicamente mediante `.env`.
- Los errores del panel ADMIN se muestran en pantalla para facilitar la demostración.

### Gmail real
Copia `.env.example` a `.env` y configura `MAIL_USERNAME` y `MAIL_PASSWORD` con una contraseña de aplicación nueva. No subas `.env` a Git. Si una contraseña de aplicación fue compartida en un chat, revócala y genera otra.

## Mejoras del panel administrador

Esta versión permite configurar SMTP desde la interfaz de administrador (la contraseña de aplicación se cifra con AES-256 antes de persistirse), probar el envío, crear otros usuarios ADMIN y cambiar el nombre/contraseña del administrador. El dashboard incluye gráfica circular de avance del onboarding y barras de usuarios por estado.

Al iniciar una base limpia se crean cinco usuarios de demostración para que el dashboard tenga datos desde el primer arranque. Sus correos terminan en `@demo.local` y la contraseña de demostración es `Demo2026!`. Estos usuarios son únicamente datos ficticios de prueba.

Para Gmail: en **Panel administrador → Configuración de correo**, captura el correo remitente y una contraseña de aplicación vigente. No uses la contraseña normal de tu cuenta. Después usa **Probar conexión** y finalmente **Guardar SMTP**.

## Correo de recuperación desde el panel administrador
1. Inicia sesión como ADMIN y abre **Configuración de correo**.
2. Para Gmail usa `smtp.gmail.com`, puerto `587`, tu correo completo y una **contraseña de aplicación** de Google (no la contraseña normal).
3. Escribe un correo real en **Correo para prueba** y pulsa **Probar conexión**. Debe llegar el mensaje antes de continuar.
4. Pulsa **Guardar SMTP**. La contraseña SMTP queda cifrada en PostgreSQL mediante el servicio AES del proyecto y nunca se devuelve al navegador.
5. Un cliente puede usar **¿Olvidaste tu contraseña?**; el sistema envía un enlace de un solo uso, con expiración de 20 minutos, al correo registrado en su cuenta.

Si se usa Outlook/Microsoft 365, selecciona Outlook y usa `smtp.office365.com:587`. La cuenta debe permitir autenticación SMTP.

## Diseño del panel administrador
El panel administrativo fue rediseñado con navegación lateral, cabecera corporativa, tarjetas KPI, gráfica circular de avance, barras por estado, tabla de usuarios, actividad reciente, configuración SMTP, gestión de administradores y expediente de clientes. Conserva las funciones existentes del backend y los permisos ADMIN.

## Diseño final de interfaz
- El inicio de sesión utiliza el fondo corporativo `frontend/public/login-background.png`, con tarjeta translúcida, recordar usuario, mostrar/ocultar contraseña y recuperación de contraseña.
- El panel ADMIN utiliza sidebar corporativo, métricas, gráfica de onboarding, barras por estado, usuarios recientes, auditoría, gestión de administradores y configuración SMTP.

## Verificación antes de entregar
Ejecutar `docker compose up --build`. Para medir cobertura del backend ejecutar `mvn test` dentro de `backend` y revisar el reporte JaCoCo. El requisito del examen es alcanzar al menos 60%; no debe asumirse cumplido sin revisar el reporte generado.

## Usuarios de demostración
La base inicial crea 10 usuarios de demostración con contraseña común `Demo2026!`: Ana López, Carlos Méndez, María García, Jorge Ramírez, Sofía Hernández, Diego Morales, Valeria Torres, Luis Castillo, Daniela Sánchez y Ricardo Flores. Los correos usan el dominio `@demo.local` y son únicamente para pruebas locales.

### Nota sobre SMTP
En el panel administrador, **Correo remitente** y **Correo para prueba** son campos independientes. El refresco automático del dashboard no sobrescribe los valores que el administrador está capturando. `Probar conexión` utiliza los valores visibles del formulario y `Guardar SMTP` persiste la configuración; si la contraseña ya fue guardada, puede dejarse vacía para conservarla.

## Corrección de persistencia de datos de usuario
Al cargar un perfil con un código postal que no existe en el catálogo postal local, el frontend ya no borra `state`, `municipality` ni `settlement`. Los datos guardados en PostgreSQL se conservan y se muestran; para CP no catalogados la ubicación queda editable manualmente.

## Corrección de aislamiento entre perfil y documentos
Las operaciones de cargar, reemplazar, descargar o eliminar documentos refrescan únicamente la lista de documentos y la auditoría. No vuelven a cargar ni sobrescriben el estado de información personal/dirección del formulario.

## Procedimiento de la actividad y diagrama de flujo

El procedimiento completo requerido para la actividad, documentado en Markdown e incluyendo el diagrama de flujo Mermaid, se encuentra en:

- `docs/procedimiento-onboarding.md`

GitHub renderiza el bloque Mermaid como diagrama visual al consultar el archivo Markdown.

## Rama requerida para la entrega

El código de la entrega debe publicarse en la rama **`desarrolloca`** del repositorio privado.

Si la rama todavía no existe, desde la raíz del repositorio puede crearse y publicarse con:

```bash
git checkout -b desarrolloca
git add .
git commit -m "Entrega Sistema de Onboarding Digital"
git push -u origin desarrolloca
```

Si la rama ya existe:

```bash
git checkout desarrolloca
git add .
git commit -m "Actualización entrega Onboarding Digital"
git push origin desarrolloca
```

Verificación:

```bash
git branch --show-current
```

El resultado esperado es `desarrolloca`.
