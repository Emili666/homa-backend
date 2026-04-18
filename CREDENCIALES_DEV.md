# Variables de Entorno — HOMA Backend

## Azure App Service (homabackend)

| Variable | Valor |
|---|---|
| `DB_HOST` | `38.242.225.58` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `homa_avanzada_db` |
| `DB_USERNAME` | `homa_user` |
| `DB_PASSWORD` | `emili_password_homa` |
| `DB_USE_SSL` | `false` |
| `JPA_DDL_AUTO` | `update` |
| `JPA_SHOW_SQL` | `false` |
| `JWT_SECRET` | `development-secret-key-not-for-production-change-in-production` |
| `JWT_EXPIRATION` | `3600000` |
| `MAIL_USERNAME` | `alojamientohoma@gmail.com` |
| `MAIL_PASSWORD` | `mbib geat hzce luev` |
| `FRONTEND_BASE_URL` | `https://d3duuewq1nioxx.cloudfront.net` |
| `BACKEND_BASE_URL` | `https://homabackend-ane5d8fueybudfaj.canadacentral-01.azurewebsites.net` |
| `CORS_ALLOWED_ORIGINS` | `https://d3duuewq1nioxx.cloudfront.net` |
| `CLOUDINARY_CLOUD_NAME` | `dczjcspmj` |
| `CLOUDINARY_API_KEY` | `966558555964388` |
| `CLOUDINARY_API_SECRET` | `oIXB_nDMg2dmXC51yJyaDLv0xSI` |
| `MERCADO_PAGO_ACCESS_TOKEN` | `TEST-534908355416854-040719-b56a4bc0917e07ab3865b2b40dc9cd51-3089380305` |
| `MERCADO_PAGO_PUBLIC_KEY` | `TEST-ce741194-c182-4504-ab65-f2d32784bb69` |
| `MERCADO_PAGO_BACK_URL_SUCCESS` | `https://d3duuewq1nioxx.cloudfront.net/success` |
| `MERCADO_PAGO_BACK_URL_PENDING` | `https://d3duuewq1nioxx.cloudfront.net/pending` |
| `MERCADO_PAGO_BACK_URL_FAILURE` | `https://d3duuewq1nioxx.cloudfront.net/failure` |
| `TURNSTILE_SECRET` | `0x4AAAAAACnmLaIWOjA6yIuhnaMWYp5eLcY` |
| `ADMIN_EMAIL` | `superadmin@homa.com` |
| `ADMIN_PASSWORD` | `admin123` |
| `SPRING_PROFILES_ACTIVE` | `prod` |

## Base de Datos VPS (MariaDB Docker)

- Host: `38.242.225.58`
- Puerto: `3306`
- BD: `homa_avanzada_db`
- Usuario: `homa_user`
- Contraseña: `emili_password_homa`
- Contenedor: `homa-db`

Acceso:
```bash
docker exec -it homa-db mariadb -u homa_user -p homa_avanzada_db
```

## Azure App Service

- Nombre: `homabackend`
- Resource Group: `homaabackend_group`
- URL: `https://homabackend-ane5d8fueybudfaj.canadacentral-01.azurewebsites.net`
- Subscription ID: `6e823056-25c9-40f6-91ee-c5021690f575`

## Frontend (AWS CloudFront + S3)

- URL: `https://d3duuewq1nioxx.cloudfront.net`
- Bucket S3: `homa-angular-deploy-homa`
- Region: `us-east-2`

## Grafana / Prometheus (VPS)

- Grafana: `http://38.242.225.58:3030`
- Prometheus: `http://38.242.225.58:9090`
- Usuario Grafana: `admin`

## Azure Service Principal (GitHub Actions)

- Client ID: `37477454-8d24-4cbe-9f5f-64dd24a38f70`
- Tenant ID: `5351d6c9-e0c1-466d-b285-cc61afcfedb6`
- Subscription ID: `6e823056-25c9-40f6-91ee-c5021690f575`
