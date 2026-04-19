# HOMA — Diseño, Reutilización y Pruebas del Software
**Curso:** Ingeniería de Software III — Universidad del Quindío  
**Versión:** 1.00 | **Fecha:** 17/04/2026  
**Autores:** Sarita Londoño Perdomo · Juan Pablo Castro Vanegas · Emili García Bermúdez

---

## 1. INTRODUCCIÓN

El presente documento establece el diseño de auditoría, la estrategia de reutilización y el plan de pruebas del sistema HOMA, una plataforma web para la gestión de alojamientos turísticos en el Quindío.

### 1.1 Alcance

El documento cubre los siguientes procesos de negocio:
- Gestión de Usuario y Autenticación
- Gestión de Alojamientos
- Gestión de Reservas y Pagos

**Fases cubiertas:** análisis, diseño, implementación, pruebas y despliegue.  
**Componentes:** backend Spring Boot (Java 17), frontend Angular 17, base de datos MariaDB.

### 1.2 Objetivos

Verificar que los tres procesos de negocio de HOMA cumplen los requisitos funcionales y no funcionales, aplicando pruebas funcionales, de integración y no funcionales alineadas con ISO/IEC 25010.

### 1.3 Glosario de Términos

| Término | Definición |
|---|---|
| JWT | Token de autenticación basado en JSON Web Token |
| CORS | Control de acceso entre dominios (Cross-Origin Resource Sharing) |
| Prometheus | Herramienta de recolección de métricas del sistema |
| Grafana | Herramienta de visualización de métricas |
| MercadoPago | Pasarela de pagos utilizada en modo sandbox (pruebas) |
| Sandbox | Entorno de pruebas sin dinero real |
| ISO/IEC 25010 | Estándar de calidad del producto software |
| RNF | Requisito No Funcional |
| CI/CD | Integración y despliegue continuo mediante GitHub Actions |

---

## 2. INFORMACIÓN DEL CASO DE ESTUDIO

**HOMA** es una plataforma digital orientada a la gestión de alojamientos turísticos y propiedades en renta en el departamento del Quindío, Colombia. Su actividad económica se enmarca en el sector tecnológico aplicado al turismo.

Los tres procesos seleccionados son críticos porque:
- **Autenticación** involucra seguridad de datos personales
- **Alojamientos** garantiza integridad de la información publicada
- **Reservas y Pagos** maneja transacciones financieras reales

---

## 3. DISEÑO AUDITORÍA

### 3.1 Objetivo de la Auditoría

Verificar que el sistema HOMA cumple los requisitos funcionales definidos, evidencia trazabilidad entre artefactos y mantiene buenas prácticas de documentación, pruebas y despliegue en los tres procesos de negocio implementados.

### 3.2 Auditoría del Software

| Criterio | Descripción |
|---|---|
| Cumplimiento de requisitos | Se implementaron autenticación JWT, CRUD de alojamientos, reservas y pagos con MercadoPago |
| Trazabilidad | Existe correspondencia entre RNF del plan de calidad y métricas visibles en Grafana |
| Calidad del código | Arquitectura en capas (controller/service/repository), Lombok, MapStruct, Bean Validation |
| Documentación completa | README, DEVOPS_DEPLOY_README, Swagger en `/swagger-ui.html`, CREDENCIALES_DEV.md |
| Resultados de pruebas | Pruebas manuales funcionales ejecutadas, métricas activas en Grafana/Prometheus |
| Evidencia de reutilización | Spring Boot starters, Cloudinary SDK, MercadoPago SDK, Tailwind CSS |
| Uso de estándares | REST API, JWT, BCrypt, ISO/IEC 25010 |

### 3.3 Características de la Auditoría

- Revisión de checklist del código en GitHub
- Revisión de métricas en Grafana (`http://38.242.225.58:3030`)
- Pruebas manuales en entorno de producción (CloudFront + Azure App Service)
- Evaluación cruzada entre integrantes del equipo

### 3.4 Tipos de Auditoría

- **Auditoría de producto:** verifica funcionalidad implementada vs requisitos definidos
- **Auditoría de proceso:** verifica CI/CD con GitHub Actions, control de versiones Git

### 3.5 Instrumento de Auditoría (Checklist)

| Ítem auditado | Evidencia | Cumple | Observaciones |
|---|---|---|---|
| Requisitos funcionales completos | Código en GitHub | Sí | N/A |
| Casos de prueba funcionales | Pruebas manuales documentadas | Sí | Faltan pruebas automatizadas |
| Despliegue CI/CD | GitHub Actions `deploy.yml` | Sí | Backend Azure, Frontend S3/CloudFront |
| Métricas de calidad | Dashboard Grafana | Sí | Prometheus scrapeando Azure |
| Seguridad JWT | `SecurityConfig.java` | Sí | Roles HUESPED/ANFITRION/ADMINISTRADOR |
| Documentación API | Swagger UI | Sí | Disponible en producción |
| Control de versiones | Repositorio GitHub | Sí | Commits con prefijos convencionales |

### 3.6 Responsables y Tiempos

| Responsable | Rol | Momento |
|---|---|---|
| Emili García | DevOps / Backend | Al finalizar cada sprint |
| Sarita Londoño | Frontend | Antes de cada entrega parcial |
| Juan Pablo Castro | Backend / BD | Revisión final antes de entrega |

---

## 4. ESTRATEGIA DE REUTILIZACIÓN

### 4.1 Objetivo

Optimizar el desarrollo de los tres procesos de negocio mediante el uso de funciones, validaciones y componentes reutilizables ya existentes en librerías, marcos de trabajo y módulos del propio sistema.

### 4.2 Tipos de Elementos Reutilizables

| Componente | Tipo | Fuente | Proceso | Justificación |
|---|---|---|---|---|
| Spring Boot Security | Framework | Spring | Autenticación | Manejo completo de seguridad sin implementar desde cero |
| JWT (jjwt) | Librería | io.jsonwebtoken | Autenticación | Generación y validación de tokens estándar |
| MapStruct | Librería | mapstruct.org | Todos | Mapeo automático entre entidades y DTOs |
| Cloudinary SDK | API externa | cloudinary.com | Alojamientos | Gestión de imágenes en la nube |
| MercadoPago SDK | API externa | mercadopago.com | Pagos | Procesamiento de pagos sin implementar pasarela propia |
| Micrometer/Prometheus | Librería | micrometer.io | Métricas | Instrumentación automática de métricas del sistema |
| Tailwind CSS | Framework CSS | tailwindcss.com | Frontend | Diseño responsive sin CSS personalizado |
| Angular Router/Guards | Framework | Angular | Frontend | Control de acceso por roles en rutas |
| `MercadoPagoButtonComponent` | Componente propio | Código HOMA | Pagos | Componente standalone reutilizable en cualquier vista |
| `BotonFavoritoComponent` | Componente propio | Código HOMA | Alojamientos | Reutilizado en listados y detalle |
| `ApiResponse<T>` | Clase genérica | Código HOMA | Todos | Respuesta estándar para todos los endpoints REST |
| JWT Interceptor Angular | Interceptor | Código HOMA | Todos | Agrega token automáticamente a todas las peticiones HTTP |

### 4.3 Criterios para Seleccionar Componentes Reutilizables

- Compatible con Java 17 / Angular 17
- Licencia open source o uso permitido
- Documentación oficial disponible
- Probado en producción por la comunidad
- Permite mantenimiento y adaptación

### 4.4 Relación con los Procesos de Negocio

- **Autenticación:** Spring Security + JWT eliminan implementación manual de sesiones y cifrado
- **Alojamientos:** Cloudinary evita gestionar almacenamiento de imágenes en servidor propio
- **Reservas/Pagos:** MercadoPago SDK maneja toda la lógica financiera y cumplimiento regulatorio

### 4.5 Buenas Prácticas Implementadas

- Separación en capas (controller / service / repository / mapper)
- Componentes Angular standalone reutilizables
- Interceptores HTTP centralizados para JWT y manejo de errores
- Clase genérica `ApiResponse<T>` para estandarizar respuestas
- Uso de `@Value` para externalizar configuración (sin hardcodear valores)
- Patrón DTO para separar entidades de la capa de presentación

---

## 5. PLAN DE PRUEBAS DEL SISTEMA

### 5.1 Objetivo

Garantizar que los procesos de autenticación, gestión de alojamientos y reservas/pagos de HOMA funcionan correctamente en el entorno de producción (Azure App Service + CloudFront).

### 5.2 Estrategia de Pruebas

Se aplican pruebas funcionales manuales sobre los tres procesos de negocio. Las pruebas no funcionales se realizan mediante observación de métricas en Grafana/Prometheus. No se usan herramientas de automatización en esta versión.

### 5.3 Ambiente de Pruebas

| Componente | URL / Detalle |
|---|---|
| Frontend | `https://d3duuewq1nioxx.cloudfront.net` |
| Backend | `https://homabackend-ane5d8fueybudfaj.canadacentral-01.azurewebsites.net` |
| Base de datos | MariaDB 10.11 en VPS Contabo `38.242.225.58:3306` |
| Monitoreo | Grafana `http://38.242.225.58:3030` |
| Navegadores | Chrome, Brave, Firefox |
| SO | Windows 11 |
| Pagos | MercadoPago Sandbox (sin dinero real) |

### 5.4 Casos de Prueba por Proceso

Ver sección 6 — Diseño de Casos de Prueba.

### 5.5 Criterios de Aceptación

- ≥ 90% de casos de prueba aprobados
- 0 errores críticos en flujos principales
- Tiempo de respuesta del login ≤ 1000ms (p95 en Grafana)
- Backend responde `{"status":"UP"}` en `/actuator/health`
- Reserva creada correctamente y botón MercadoPago visible

### 5.6 Evidencia de Pruebas

- Capturas de pantalla del sistema funcionando en producción
- Dashboard Grafana con métricas activas (Estado Backend = ACTIVO)
- Logs de GitHub Actions con deploy exitoso
- Capturas del flujo completo: login → buscar → reservar → pagar

---

## 6. DISEÑO DE CASOS DE PRUEBA

### 6.1 Relación con Requisitos Funcionales

| Casos | Proceso | Requisito |
|---|---|---|
| CP-001 a CP-003 | Gestión de Usuario y Autenticación | RNF-SEG-01, RNF-SEG-02, RNF-REN-07 |
| CP-004 a CP-006 | Gestión de Alojamientos | RNF-CON-06, RNF-REN-09 |
| CP-007 a CP-010 | Gestión de Reservas y Pagos | RNF-CON-06, RNF-USA-13 |

### 6.2 Casos de Prueba

#### CP-001: Login con credenciales válidas

| Ítem | Descripción |
|---|---|
| Número | CP-001 |
| Título | Login exitoso con credenciales válidas |
| Proceso | Gestión de Usuario y Autenticación |
| Objetivo | Verificar que el sistema autentica y retorna JWT válido |
| Requisito | RNF-SEG-01, RNF-REN-07 |
| Tester | Emili García |
| Fecha | 17/04/2026 |
| Precondiciones | Usuario registrado y activo en BD |
| Entradas | email: `emilibermudez6@gmail.com`, contraseña válida |
| SO | Windows 11 |
| Browser | Chrome |
| Prioridad | Alta |
| Asignado a | Emili García |

#### CP-002: Login con credenciales inválidas

| Ítem | Descripción |
|---|---|
| Número | CP-002 |
| Título | Login con contraseña incorrecta retorna 401 |
| Proceso | Autenticación |
| Objetivo | Verificar que el sistema rechaza credenciales inválidas |
| Requisito | RNF-SEG-02 |
| Entradas | email válido, contraseña incorrecta |
| Resultado esperado | HTTP 401, mensaje "Credenciales inválidas" |
| Prioridad | Alta |

#### CP-003: Registro de nuevo usuario

| Ítem | Descripción |
|---|---|
| Número | CP-003 |
| Título | Registro exitoso de nuevo huésped |
| Proceso | Autenticación |
| Objetivo | Verificar que se crea el usuario y se envía email de activación |
| Requisito | RNF-SEG-01 |
| Entradas | nombre, email nuevo, contraseña, teléfono |
| Resultado esperado | Usuario creado, email de confirmación enviado |
| Prioridad | Alta |

#### CP-004: Crear alojamiento

| Ítem | Descripción |
|---|---|
| Número | CP-004 |
| Título | Anfitrión crea alojamiento con información completa |
| Proceso | Gestión de Alojamientos |
| Objetivo | Verificar que se registra el alojamiento correctamente |
| Requisito | RNF-CON-06 |
| Precondiciones | Usuario con rol ANFITRION autenticado |
| Entradas | título, descripción, ciudad (dropdown Quindío), precio, imágenes |
| Resultado esperado | Alojamiento creado con estado PENDIENTE |
| Prioridad | Alta |

#### CP-005: Buscar alojamientos por ciudad

| Ítem | Descripción |
|---|---|
| Número | CP-005 |
| Título | Búsqueda de alojamientos por municipio del Quindío |
| Proceso | Gestión de Alojamientos |
| Objetivo | Verificar que el filtro por ciudad retorna resultados correctos |
| Requisito | RNF-REN-09 |
| Entradas | ciudad: "Armenia", fechas disponibles |
| Resultado esperado | Lista de alojamientos en Armenia en ≤ 1500ms |
| Prioridad | Media |

#### CP-006: Verificar disponibilidad de alojamiento

| Ítem | Descripción |
|---|---|
| Número | CP-006 |
| Título | Consulta de disponibilidad con fechas ocupadas |
| Proceso | Gestión de Alojamientos / Reservas |
| Objetivo | Verificar que el sistema detecta conflicto de fechas |
| Requisito | RNF-USA-13 |
| Entradas | alojamientoId con reserva existente, mismas fechas |
| Resultado esperado | Sistema indica no disponible |
| Prioridad | Alta |

#### CP-007: Crear reserva exitosa

| Ítem | Descripción |
|---|---|
| Número | CP-007 |
| Título | Huésped crea reserva con fechas disponibles |
| Proceso | Gestión de Reservas |
| Objetivo | Verificar que se crea la reserva y navega al pago |
| Requisito | RNF-CON-06, RNF-USA-13 |
| Precondiciones | Usuario HUESPED autenticado, alojamiento ACTIVO disponible |
| Entradas | fechaEntrada, fechaSalida, cantidadHuespedes |
| Resultado esperado | Reserva creada en estado PENDIENTE, redirección a `/reservas/pago` |
| Prioridad | Alta |

#### CP-008: Pago con MercadoPago Sandbox

| Ítem | Descripción |
|---|---|
| Número | CP-008 |
| Título | Pago exitoso con tarjeta de prueba MercadoPago |
| Proceso | Pagos |
| Objetivo | Verificar que el botón de MercadoPago carga y procesa el pago |
| Requisito | RNF-CON-06 |
| Precondiciones | Reserva creada, usuario en página `/reservas/pago` |
| Entradas | Tarjeta: `4509 9535 6623 3704`, CVV: `123`, nombre: `APRO` |
| Resultado esperado | Pago aprobado, redirección a historial de reservas |
| Prioridad | Alta |

#### CP-009: Anfitrión confirma reserva

| Ítem | Descripción |
|---|---|
| Número | CP-009 |
| Título | Anfitrión confirma reserva pendiente |
| Proceso | Gestión de Reservas |
| Objetivo | Verificar cambio de estado PENDIENTE → CONFIRMADA |
| Requisito | RNF-CON-06 |
| Precondiciones | Reserva en estado PENDIENTE, usuario ANFITRION autenticado |
| Resultado esperado | Estado cambia a CONFIRMADA, huésped recibe email |
| Prioridad | Alta |

#### CP-010: Cancelar reserva

| Ítem | Descripción |
|---|---|
| Número | CP-010 |
| Título | Huésped cancela reserva con más de 48h de anticipación |
| Proceso | Gestión de Reservas |
| Objetivo | Verificar que la cancelación funciona dentro del plazo permitido |
| Requisito | RNF-CON-06 |
| Entradas | reservaId con fecha de entrada > 48h desde ahora |
| Resultado esperado | Estado cambia a CANCELADA |
| Prioridad | Media |

---

## 7. ESTRATEGIAS Y TÉCNICAS DE PRUEBAS

### 7.1 Estrategia General

Pruebas funcionales manuales por capas: primero validación de servicios backend via Swagger, luego integración frontend-backend en producción, finalmente flujo completo de sistema.

### 7.2 Técnicas Aplicadas

| Técnica | Tipo | Proceso |
|---|---|---|
| Partición de clases de equivalencia | Caja negra | Validación de formularios (login, registro, alojamiento) |
| Análisis de valores límite | Caja negra | Fechas de reserva, capacidad de huéspedes, precios |
| Pruebas basadas en casos de uso | Caja negra | Flujo completo de reserva y pago |
| Recorrido de caminos | Caja blanca | Flujo de autenticación en `AuthServiceImpl.java` |
| Cobertura de condiciones | Caja blanca | Validaciones en `ReservaServiceImpl.java` |

### 7.3 Cobertura Esperada

Un flujo principal y dos flujos alternos por proceso:
- Login: exitoso / credenciales inválidas / cuenta inactiva
- Alojamiento: crear exitoso / campos faltantes / sin imágenes
- Reserva: crear exitoso / fechas ocupadas / cancelación tardía

### 7.4 Justificación

Se eligió partición de equivalencia para formularios porque permite cubrir entradas válidas e inválidas eficientemente. Casos de uso para reservas porque el flujo involucra múltiples pasos encadenados (crear → pagar → confirmar → completar).

### 7.5 Casos de Prueba Referenciados

| ID | Proceso | Técnica | Tipo |
|---|---|---|---|
| CP-001 | Autenticación | Valores límite | Funcional |
| CP-002 | Autenticación | Partición equivalencia | Funcional |
| CP-004 | Alojamientos | Caso de uso | Funcional |
| CP-007 | Reservas | Caso de uso | Sistema |
| CP-008 | Pagos | Caso de uso | Sistema |
| PN-02 | Autenticación | Métricas Grafana | Rendimiento |

---

## 8. TIPOS DE PRUEBA DEFINIDOS

### 8.1 Listado y Descripción

| Tipo | Descripción | Aplicación en HOMA |
|---|---|---|
| Pruebas funcionales | Verifican requisitos funcionales | Login, registro, crear alojamiento, hacer reserva, pagar |
| Pruebas de integración | Verifican comunicación entre módulos | Angular ↔ Spring Boot ↔ MariaDB |
| Pruebas de validación de datos | Entradas inválidas y fuera de rango | Formularios con campos vacíos, fechas incorrectas |
| Pruebas de rendimiento | Tiempo de respuesta bajo carga | Métricas Grafana: latencia p95 login ≤ 1000ms |
| Pruebas de seguridad | Acceso no autorizado | Endpoints protegidos retornan 401/403 sin JWT |
| Pruebas de compatibilidad | Múltiples navegadores y dispositivos | Chrome, Brave, Firefox en desktop y móvil |

### 8.2 Relación con Procesos de Negocio

| Proceso | Tipos de Prueba | Justificación |
|---|---|---|
| Autenticación | Funcionales, seguridad, rendimiento | Credenciales, tokens JWT, tiempo de login |
| Gestión de Alojamientos | Funcionales, integración, compatibilidad | CRUD, imágenes Cloudinary, responsive |
| Reservas y Pagos | Funcionales, integración, rendimiento | Flujo completo, MercadoPago, disponibilidad |

---

## 9. GUÍA DE ESTRATEGIA DE DOCUMENTACIÓN

### 9.1 Objetivo

Establecer criterios, herramientas y formatos para documentar de forma clara y consistente el desarrollo de los tres procesos de negocio de HOMA.

### 9.2 Elementos Documentados

- Requisitos funcionales y no funcionales (Plan de Gestión de Calidad)
- API REST documentada con Swagger/OpenAPI en `/swagger-ui.html`
- README.md con instrucciones de instalación y ejecución
- DEVOPS_DEPLOY_README.md con guía de despliegue
- CREDENCIALES_DEV.md con variables de entorno
- Este documento (Diseño, Reutilización y Pruebas)
- Código comentado en servicios críticos (`AuthServiceImpl`, `ReservaServiceImpl`)

### 9.3 Herramientas

| Herramienta | Uso |
|---|---|
| GitHub | Control de versiones y CI/CD |
| Swagger/OpenAPI | Documentación de API REST |
| Google Docs | Documentos académicos |
| Grafana | Evidencia visual de métricas |
| Markdown (.md) | Documentación técnica en repositorio |

### 9.4 Convenciones y Formatos

- Commits: `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`
- Versiones: v1.0, v1.1
- Nombres de archivos: `NOMBRE_EN_MAYUSCULAS.md`
- Ramas: `master` (producción), `feature/nombre` (desarrollo)

### 9.5 Responsables

| Integrante | Responsabilidad |
|---|---|
| Emili García | DevOps, métricas, despliegue |
| Sarita Londoño | Frontend Angular, documentación UI |
| Juan Pablo Castro | Backend Spring Boot, base de datos |

### 9.6 Frecuencia de Actualización

La documentación se actualiza al finalizar cada sprint y se revisa antes de cada entrega parcial al docente.

---

## 10. DISEÑO DE PRUEBAS NO FUNCIONALES POR ATRIBUTO DE CALIDAD

### 10.1 Atributos Seleccionados

| Atributo | Descripción | Justificación |
|---|---|---|
| Seguridad | Protección de datos y acceso controlado | Manejo de datos personales y transacciones financieras |
| Eficiencia de Desempeño | Tiempo de respuesta ante solicitudes | Tiempos críticos en búsquedas, reservas y pagos |
| Confiabilidad | Disponibilidad y ausencia de fallos | Evitar dobles reservas y fallos en transacciones |
| Compatibilidad | Funcionamiento en múltiples entornos | Plataforma web accesible desde distintos dispositivos |
| Usabilidad | Facilidad de uso para el usuario final | Usuarios con distintos niveles de experiencia tecnológica |

### 10.2 Diseño de Pruebas por Atributo

#### PN-01: Seguridad — Acceso sin token

| Campo | Descripción |
|---|---|
| ID | PN-01 |
| Atributo | Seguridad |
| Objetivo | Verificar que endpoints protegidos retornan 401 sin JWT |
| Métrica | 100% de endpoints protegidos retornan 401 Unauthorized |
| Entorno | Chrome, producción Azure |
| Pasos | 1. Abrir Swagger UI. 2. Ejecutar GET `/api/reservas/mis-reservas` sin token. 3. Verificar respuesta |
| Resultado esperado | HTTP 401 con mensaje "Se requiere autenticación" |

#### PN-02: Eficiencia de Desempeño — Tiempo de login

| Campo | Descripción |
|---|---|
| ID | PN-02 |
| Atributo | Eficiencia de Desempeño |
| Objetivo | Verificar que el login responde en ≤ 1000ms (percentil 95) |
| Métrica | p95 ≤ 1.0 segundo en panel Grafana |
| Entorno | Grafana `http://38.242.225.58:3030`, panel "Latencia p95" |
| Pasos | 1. Abrir Grafana. 2. Ir a dashboard HOMA. 3. Observar panel "Latencia p95". 4. Realizar 5 logins consecutivos |
| Resultado esperado | Valor p95 ≤ 1.0s en condiciones normales de operación |

#### PN-03: Confiabilidad — Disponibilidad del servicio

| Campo | Descripción |
|---|---|
| ID | PN-03 |
| Atributo | Confiabilidad |
| Objetivo | Verificar disponibilidad ≥ 99% del backend en Azure |
| Métrica | Panel "Estado Backend" muestra ACTIVO (valor = 1) |
| Entorno | Grafana, panel "Estado General" |
| Pasos | 1. Abrir Grafana. 2. Verificar panel "Estado Backend". 3. Consultar `/actuator/health` |
| Resultado esperado | Estado = ACTIVO, `/actuator/health` retorna `{"status":"UP"}` |

#### PN-04: Compatibilidad — Multi navegador

| Campo | Descripción |
|---|---|
| ID | PN-04 |
| Atributo | Compatibilidad |
| Objetivo | Verificar funcionamiento correcto en Chrome, Firefox y Brave |
| Métrica | ≥ 95% de funcionalidades operativas en cada navegador |
| Entorno | CloudFront `https://d3duuewq1nioxx.cloudfront.net` |
| Pasos | 1. Abrir la URL en cada navegador. 2. Hacer login. 3. Buscar alojamiento. 4. Crear reserva |
| Resultado esperado | Sin errores visuales ni funcionales en ningún navegador |

#### PN-05: Usabilidad — Completar reserva en tiempo límite

| Campo | Descripción |
|---|---|
| ID | PN-05 |
| Atributo | Usabilidad |
| Objetivo | Verificar que un usuario completa el flujo de reserva en ≤ 3 minutos |
| Métrica | Tiempo promedio desde login hasta confirmación de pago ≤ 3 minutos |
| Entorno | CloudFront, Chrome, usuario nuevo |
| Pasos | 1. Iniciar sesión. 2. Buscar alojamiento. 3. Seleccionar fechas. 4. Crear reserva. 5. Completar pago en MercadoPago sandbox |
| Resultado esperado | Flujo completo sin errores en menos de 3 minutos |

### 10.3 Relación con los Procesos de Negocio

| Prueba | Proceso | Relación |
|---|---|---|
| PN-01 | Autenticación | Valida que el control de acceso JWT funciona correctamente |
| PN-02 | Autenticación | Mide el tiempo de respuesta del endpoint `/api/auth/login` |
| PN-03 | Todos | Verifica disponibilidad general del backend en Azure |
| PN-04 | Todos | Garantiza acceso desde distintos navegadores y dispositivos |
| PN-05 | Reservas y Pagos | Evalúa la experiencia del usuario en el flujo crítico de reserva |

---

*Universidad del Quindío — Programa de Ingeniería de Sistemas y Computación*  
*Carrera 15 Calle 12 Norte, Armenia, Quindío — ingesis@uniquindio.edu.co*
