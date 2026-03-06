# Proyecto HOMA - Sistema de Gestión de Alojamientos

Este repositorio contiene el backend (Spring Boot) y el frontend (Angular) del proyecto **HOMA**, diseñado para facilitar la gestión de reservas y alojamientos.

## 📁 Estructura del Proyecto

```text
/
├── homa-backend/      # API REST (Java 17 / Spring Boot 3.4.2)
├── homa-frontend/     # Interfaz de Usuario (Angular 17)
├── docker-compose.yml # Infraestructura MariaDB para el VPS
└── .env.example       # Template de variables de entorno
```

---

## 🚀 Despliegue en 3 Pasos

### 1. Despliegue Completo (BD + Backend + Frontend en un solo paso)
1. Instala **Docker** y **Docker Compose** en tu servidor.
2. Sube `docker-compose.prod.yml` y `.env` al servidor.
3. Ejecuta: `docker compose -f docker-compose.prod.yml up -d`

### 2. Base de Datos Individual (en tu VPS)

### 2. Backend (en la Nube - p.ej. Railway, Render)
1. Conecta este repositorio a tu servicio de nube.
2. Define el directorio raíz como `homa-backend/`.
3. Configura las **Variables de Entorno** en el panel de tu nube (usa `.env.example` como guía).
   - Asegúrate de poner la IP de tu VPS en `DB_HOST`.
4. El comando de arranque debe ser: `./gradlew bootRun` (o usar Docker si el servicio lo permite).

### 3. Frontend (en la Nube - p.ej. Vercel, Netlify)
1. Conecta este repositorio.
2. Define el directorio raíz como `homa-frontend/`.
3. Configura el comando de construcción: `npm run build`.
4. Define el directorio de salida: `dist/homa-frontend`.

---

## 🛠️ DevOps y Despliegue Cloud Free

### 1. Backend (Render.com - Gratis)
1. Crea una cuenta en [Render](https://render.com).
2. Crea un nuevo **Web Service** y conecta tu repositorio `homa-backend`.
3. Render detectará automáticamente el `Dockerfile`.
4. Configura las variables de entorno en el panel (usando `.env.example`).
5. El backend se desplegará automáticamente en cada `git push`.

### 2. Frontend (Vercel.com - Gratis)
1. Crea una cuenta en [Vercel](https://vercel.com).
2. Importa tu repositorio `homa-frontend`.
3. Configura el comando de construcción: `npm run build`.
4. El frontend se desplegará en una URL tipo `homa-frontend.vercel.app`.

### 3. Integración Continua (CI)
Hemos configurado **GitHub Actions**. Cada vez que realices cambios:
- Se verificará que el Backend compile con Gradle.
- Se verificará que el Frontend construya correctamente su bundle.
Puedes ver el estado en la pestaña **Actions** de tus repositorios en GitHub.

---

## 🔧 Configuración para Desarrollo Local

Consulta el [Walkthrough](file:///C:/Users/Emili/.gemini/antigravity/brain/28bec87c-953a-4556-954e-142a47d9fa62/walkthrough.md) para más detalles sobre cómo instalar y levantar el proyecto localmente.

---

> **Nota para el evaluador:** Asegúrate de que las credenciales de Cloudinary y SMTP sean válidas para el correcto funcionamiento del envío de correos y carga de imágenes.
