# Documentación del Backend - HOMA

Este proyecto utiliza **Spring Boot** con una arquitectura de microservicios-ready, siguiendo patrones de diseño como DTOs, Mappers (MapStruct) y Servicios.

## 🚀 Tecnologías Principales
- **Java 17 / Spring Boot 3**
- **Spring Security + JWT**: Para autenticación y autorización.
- **Spring Data JPA**: Conexión con MariaDB.
- **MapStruct**: Mapeo eficiente entre entidades y DTOs.
- **Lombok**: Reducción de código boilerplate.
- **Springdoc OpenAPI (Swagger)**: Documentación interactiva de la API.

## 📁 Estructura del Proyecto
- `controller/`: Endpoints REST expuestos al frontend.
- `service/`: Lógica de negocio y orquestación.
- `model/`: Entidades JPA que representan las tablas de la base de datos.
- `repository/`: Interfaces para operaciones CRUD con la base de datos.
- `dto/`: Objetos de Transferencia de Datos para peticiones y respuestas.
- `util/`: Clases de utilidad como `EmailService`.

## ⚙️ Configuración Importante
El archivo `application.properties` contiene configuraciones críticas:
- Base de Datos: URL, usuario y contraseña.
- Cloudinary: Para almacenamiento de imágenes en la nube.
- SMTP: Configuración para el envío de correos de confirmación.

## 🔑 Credenciales de Administrador (Semilla)
Al iniciar, el sistema verifica y crea un administrador si no existe:
- **Email**: `superadmin@homa.com`
- **Contraseña**: `admin123`
