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

El `application.properties` ya tiene todas las credenciales configuradas.  
**No necesitas configurar nada.**

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
| Email | `superadmin@homa.com` |
| Contraseña | `admin123` |

### Usuarios de prueba en la BD
| Email | Contraseña | Rol |
|---|---|---|
| `emilibermudez6@gmail.com` | `Emili123` | Huésped |
| `eynera.diaz@uqvirtual.edu.co` | (ver con el equipo) | Anfitrión |

---

## 5. Servicios externos (ya configurados)

| Servicio | Estado |
|---|---|
| Base de datos MariaDB | VPS Contabo `38.242.225.58` — ya conectada |
| Cloudinary (imágenes) | Configurado — cloud: `dczjcspmj` |
| Mercado Pago | Sandbox TEST — no cobra dinero real |
| Correo SMTP | Gmail `alojamientohoma@gmail.com` |
| Mapas | Leaflet + OpenStreetMap — sin token |
| CAPTCHA | Cloudflare Turnstile — clave de prueba |

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

## 7. Credenciales completas

Ver archivo: `CREDENCIALES_DEV.md`

---

## Estructura del proyecto

```
homa-backend/   → Spring Boot 3.4.2 + Java 17 + MariaDB
homa-frontend/  → Angular 17 + TailwindCSS
```
