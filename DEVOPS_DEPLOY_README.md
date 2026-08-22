# Guía de Despliegue DevOps - HOMA (AWS)

Este documento detalla la infraestructura y el flujo de CI/CD para el proyecto HOMA utilizando **GitHub Actions** y **AWS**.

## 🏗️ Arquitectura de Despliegue
- **Frontend**: Alojado en **AWS S3** con distribución de **Amazon CloudFront** (CDN) para máxima velocidad y HTTPS.
- **Backend**: Desplegado en **AWS Elastic Beanstalk** (PaaS) que escala automáticamente según la carga.
- **Base de Datos**: Ubicada en la **VPS** del usuario (`38.242.225.58`), conectada mediante JDBC.

## 🚀 Flujo de CI/CD (GitHub Actions)
Cada vez que se realiza un `push` a la rama `main`, se activan los siguientes flujos automatizados:

### 1. Backend (`homa-backend`)
- **Build**: Compilación con Gradle (JDK 17).
- **Artifact**: Generación del `.jar`.
- **Deploy**: Envío automático a AWS Elastic Beanstalk.
- **Configuración**: Las variables de entorno (`DB_HOST`, `DB_PASSWORD`, etc.) deben configurarse en la consola de AWS EB.

### 2. Frontend (`homa-frontend`)
- **Build**: Compilación de producción (`ng build --configuration production`).
- **Deploy**: Sincronización de la carpeta `dist/` con el bucket de AWS S3.
- **Invalidation**: Limpieza de caché en CloudFront para mostrar los cambios inmediatamente.

## 🔐 Secretos en GitHub
Para que los flujos funcionen, debes agregar los siguientes **Action Secrets** en el repositorio:
1. `AWS_ACCESS_KEY_ID`: Tu llave de acceso de AWS IAM.
2. `AWS_SECRET_ACCESS_KEY`: Tu llave secreta de AWS IAM.
3. `CLOUDFRONT_DISTRIBUTION_ID`: El ID de tu distribución de CloudFront (para el front).

## 📡 Conexión con la Base de Datos VPS
El Backend está pre-configurado para conectarse a tu VPS. Asegúrate de que el puerto **3306** esté abierto en el Firewall de tu VPS para la IP de AWS o para `0.0.0.0/0` (con precaución).

| Variable | Valor Sugerido |
| :--- | :--- |
| `DB_HOST` | IP o hostname de tu servidor de base de datos |
| `DB_NAME` | Nombre de tu base de datos |
| `DB_USERNAME` | Usuario de tu base de datos |
| `DB_PASSWORD` | *(definida en tu .env, no se documenta aquí)* |
