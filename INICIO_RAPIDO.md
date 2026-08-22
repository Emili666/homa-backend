# 🚀 Guía de Inicio Rápido — HOMA Backend

> Proyecto académico — Universidad del Quindío  
> Sigue estos pasos para levantar el proyecto en tu máquina.

---

## Requisitos previos

- Java 17 (JDK)
- Node.js 18+ y npm
- Git

---

## 1. Clonar los repositorios

```bash
git clone https://github.com/Emili666/homa-backend.git
git clone https://github.com/Emili666/homa-frontend.git
```

---

## 2. Levantar el Backend

Copia `.env.example` como `.env` y completa las variables con tus credenciales antes de correr el proyecto.

```bash
cd homa-backend
./gradlew bootRun
```

En Windows:
```bash
gradlew.bat bootRun
```

Espera hasta ver:
```
Started HomaApplication in X seconds
```

El backend queda en: `http://localhost:8081`  
Swagger UI: `http://localhost:8081/swagger-ui.html`

---

## 3. Levantar el Frontend

```bash
cd homa-frontend
npm install
ng serve
```

El frontend queda en: `http://localhost:4200`

---

## 4. Credenciales de acceso

### Administrador
| Campo | Valor |
|---|---|
| Email | definido en `ADMIN_EMAIL` de tu `.env` |
| Contraseña | definida en `ADMIN_PASSWORD` de tu `.env` |

### Usuarios de prueba en la BD
| Email | Contraseña | Rol |
|---|---|---|
| (ver con el equipo) | (ver con el equipo) | Huésped |
| (ver con el equipo) | (ver con el equipo) | Anfitrión |

---

## 5. Servicios externos

| Servicio | Estado |
|---|---|
| Base de datos MariaDB | Configurada vía variable `DB_HOST` en `.env` |
| Cloudinary (imágenes) | Configurado vía variables `CLOUDINARY_*` en `.env` |
| Mercado Pago | Sandbox TEST — no cobra dinero real |
| Correo SMTP | Gmail — configurado vía `MAIL_USERNAME` en `.env` |
| Mapas | Leaflet + OpenStreetMap — sin token |
| CAPTCHA | Cloudflare Turnstile — configurado vía `TURNSTILE_SECRET` en `.env` |

---

## 6. Probar pagos con Mercado Pago (Sandbox)

Usa estas tarjetas de prueba en el checkout:

| Tarjeta | Número | CVV | Vencimiento | Resultado |
|---|---|---|---|---|
| Visa aprobada | `4009 1753 3280 6176` | `123` | `11/25` | ✅ Aprobado |
| Mastercard rechazada | `5031 7557 3453 0604` | `123` | `11/25` | ❌ Rechazado |

Para pagar necesitas una **cuenta compradora de prueba** de Mercado Pago.  
Créala en: https://www.mercadopago.com.co/developers/panel/test-users

---

## 7. Variables de entorno

Todas las credenciales necesarias están documentadas en `.env.example`. Copia ese archivo como `.env` y completa los valores reales.

---

## Estructura del proyecto

```
homa-backend/   → Spring Boot 3.4.2 + Java 17 + MariaDB
homa-frontend/  → Angular 17 + TailwindCSS
```
