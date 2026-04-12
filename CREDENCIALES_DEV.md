# 🔑 Credenciales de Desarrollo — HOMA Backend

> ⚠️ Proyecto académico — Universidad del Quindío  
> Este archivo es solo para uso interno del equipo de desarrollo.

---

## Base de Datos (MariaDB — VPS Contabo)

| Variable | Valor |
|---|---|
| `DB_HOST` | `38.242.225.58` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `homa_avanzada_db` |
| `DB_USERNAME` | `homa_user` |
| `DB_PASSWORD` | `emili_password_homa` |
| `DB_USE_SSL` | `false` |
| `DB_REQUIRE_SSL` | `false` |

Conexión directa:
```
jdbc:mariadb://38.242.225.58:3306/homa_avanzada_db?allowPublicKeyRetrieval=true&useSSL=false
```

---

## JWT

| Variable | Valor |
|---|---|
| `JWT_SECRET` | `development-secret-key-not-for-production-change-in-production` |
| `JWT_EXPIRATION` | `3600000` (1 hora) |

---

## Correo Gmail (SMTP)

| Variable | Valor |
|---|---|
| `MAIL_USERNAME` | `alojamientohoma@gmail.com` |
| `MAIL_PASSWORD` | `mbib geat hzce luev` |
| `MAIL_HOST` | `smtp.gmail.com` |
| `MAIL_PORT` | `587` |

> La contraseña es un **App Password** de Google, no la contraseña real de la cuenta.

---

## Mercado Pago (Sandbox — TEST)

| Variable | Valor |
|---|---|
| `MERCADO_PAGO_ACCESS_TOKEN` | `TEST-534908355416854-040719-b56a4bc0917e07ab3865b2b40dc9cd51-3089380305` |
| `MERCADO_PAGO_PUBLIC_KEY` | `TEST-ce741194-c182-4504-ab65-f2d32784bb69` |
| `MERCADO_PAGO_BACK_URL_SUCCESS` | `http://localhost:4200/success` |
| `MERCADO_PAGO_BACK_URL_PENDING` | `http://localhost:4200/pending` |
| `MERCADO_PAGO_BACK_URL_FAILURE` | `http://localhost:4200/failure` |

> Credenciales `TEST-` → no cobran dinero real.

---

## Cloudinary (Imágenes)

> ⚠️ No encontradas en el historial — completar con las keys del panel de Cloudinary:  
> https://console.cloudinary.com

| Variable | Valor |
|---|---|
| `CLOUDINARY_CLOUD_NAME` | *(completar)* |
| `CLOUDINARY_API_KEY` | *(completar)* |
| `CLOUDINARY_API_SECRET` | *(completar)* |
| `CLOUDINARY_FOLDER` | `alojamientos` |

---

## Mapbox (Mapas)

| Variable | Valor |
|---|---|
| `MAPBOX_TOKEN` | *(completar — panel: https://account.mapbox.com)* |

---

## Cloudflare Turnstile (CAPTCHA)

| Variable | Valor |
|---|---|
| `TURNSTILE_SECRET` | `0x4AAAAAACnmLaIWOjA6yIuhnaMWYp5eLcY` |

---

## Administrador inicial

| Variable | Valor |
|---|---|
| `ADMIN_EMAIL` | `superadmin@homa.com` |
| `ADMIN_PASSWORD` | `admin123` |

> Se crea automáticamente al arrancar si no existe en la BD.

---

## JPA (Desarrollo local)

| Variable | Valor |
|---|---|
| `JPA_DDL_AUTO` | `update` |
| `JPA_SHOW_SQL` | `true` |

---

## Monitoring (VPS)

| Servicio | URL | Usuario | Contraseña |
|---|---|---|---|
| Grafana | `http://38.242.225.58:3030` | `admin` | `homa_grafana_2026` |
| Prometheus | `http://38.242.225.58:9090` | — | — |

---

## application.properties local completo

Copia esto en `src/main/resources/application.properties` para desarrollo local:

```properties
spring.application.name=Homa
server.port=8081

# Base de datos
spring.datasource.url=jdbc:mariadb://38.242.225.58:3306/homa_avanzada_db?allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=homa_user
spring.datasource.password=emili_password_homa
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
spring.jpa.properties.hibernate.format_sql=true

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.paths-to-match=/api/**
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.tryItOutEnabled=true

# JWT
jwt.secret=development-secret-key-not-for-production-change-in-production
jwt.expiration=3600000

# Correo
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=alojamientohoma@gmail.com
spring.mail.password=mbib geat hzce luev
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# URLs
app.frontend.url=http://localhost:4200
app.backend.url=http://localhost:8081
app.cors.allowed-origins=http://localhost:4200

# Mapbox
mapbox.access-token=${MAPBOX_TOKEN:completar}

# Cloudinary
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME:completar}
cloudinary.api-key=${CLOUDINARY_API_KEY:completar}
cloudinary.api-secret=${CLOUDINARY_API_SECRET:completar}
cloudinary.folder=alojamientos

# Archivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true

# Actuator
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.health.show-details=when-authorized
management.endpoint.prometheus.enabled=true
management.metrics.tags.application=homa-backend
management.metrics.web.server.request.autotime.enabled=true

# Mercado Pago (Sandbox)
mercadopago.access_token=TEST-534908355416854-040719-b56a4bc0917e07ab3865b2b40dc9cd51-3089380305
mercadopago.public_key=TEST-ce741194-c182-4504-ab65-f2d32784bb69
mercadopago.back_url_success=http://localhost:4200/success
mercadopago.back_url_pending=http://localhost:4200/pending
mercadopago.back_url_failure=http://localhost:4200/failure

# Turnstile
cloudflare.turnstile.secret=0x4AAAAAACnmLaIWOjA6yIuhnaMWYp5eLcY
cloudflare.turnstile.verify-url=https://challenges.cloudflare.com/turnstile/v0/siteverify

# Admin inicial
admin.email=superadmin@homa.com
admin.password=admin123
```
