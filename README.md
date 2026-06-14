Markdown
# SGPIV - Plataforma de Gestión del Parque Industrial de Viedma

## 📝 Descripción del Proyecto
El **SGPIV** es un sistema web integral diseñado para centralizar, automatizar y digitalizar la gestión administrativa y el control de radicación de empresas en el Parque Industrial de Viedma. Reemplaza los procesos manuales dispersos (planillas y comunicaciones informales) por un flujo de software robusto, auditable y con sincronización en tiempo real.

La plataforma permite a las empresas interesadas registrarse de manera segura, postular sus proyectos productivos adjuntando documentación técnica en PDF y realizar un seguimiento en vivo de sus estados de evaluación. Asimismo, provee al Administrador municipal un panel de control unificado (Dashboard) con métricas KPI, inventario de recursos compartidos y una bandeja de entrada reactiva para evaluar, aprobar o rechazar radicaciones de manera transparente.

---

## 🚀 Características Clave y Reglas de Negocio Implementadas

* **Registro Blindado y Control de Duplicados:** Validación multinivel en la capa de persistencia (`unique = true`) y lógica preventiva en servicios (`existsByEmail`, `existsByUsername`) que impide la duplicación de identidades (Emails, DNI, CUIT), manteniendo la consistencia de la base de datos incluso en escenarios de solicitudes rechazadas.
* **Presentación de Proyectos Técnicos:** Formulario dinámico con validación de obligatoriedad de archivos binarios (PDF) para el análisis técnico del Directorio.
* **Bandeja de Entrada del Administrador (Evaluar Solicitudes):** Interfaz fluida basada en pestañas (*Tabs*) y grillas avanzadas (*Grid*) que segmenta proyectos según su estado transicional (`PENDIENTE`, `EN_EVALUACION`, `APROBADA`, `RECHAZADA`).
* **Sincronización Reactiva en Tiempo Real (Efecto WOW):** Implementación de técnicas de *Polling asíncrono* de fondo gestionado por el servidor. Permite que las modificaciones de proyectos por parte de los usuarios impacten en la grilla del Administrador, y que los cambios de estado del Admin (como abrir para evaluar) bloqueen instantáneamente la edición al usuario en vivo sin necesidad de recargar la página (F5).
* **Dashboard Municipal con KPIs Dinámicos:** Tablero de gestión con tarjetas de indicadores de solicitudes pendientes integradas al conteo real de la base de datos, grillas de control de préstamos de maquinaria y seguimiento de consumos de recursos (Energía/Agua).

---

## 🏗️ Arquitectura del Sistema
El proyecto sigue una arquitectura limpia basada en **capas de responsabilidades separadas**, respetando los estándares de desarrollo empresarial sobre Spring Boot:

* `com.unrn.gpiv.common`: Contiene diccionarios globales y estructuras de estados (`Enums` como `EstadoSolicitud`, `TipoServicio`).
* `com.unrn.gpiv.model`: El corazón del modelo de dominio. Clases de entidad JPA con herencia estructurada (ej. la superclase abstracta `Usuario` e hija `RepresentanteEmpresa`) mapeadas de forma relacional mediante metadatos y anotaciones de Jakarta Persistence.
* `com.unrn.gpiv.repository`: Interfaces de persistencia de datos que extienden de `JpaRepository`. Utiliza consultas derivadas nativas para optimizar la velocidad de acceso a la base de datos.
* `com.unrn.gpiv.service`: Capa de lógica de negocio donde se centralizan las reglas operativas, validaciones preventivas y límites transaccionales mediante el control de fronteras con `@Transactional`.
* `com.unrn.gpiv.views`: Interfaz de usuario (Frontend) reactiva basada en layouts y componentes Web UI integrados al servidor.

---

## 🛠️ Stack Tecnológico Utilizado

* **Lenguaje Principal:** Java 21+
* **Backend Framework:** Spring Boot 3.x (Spring Data JPA, Hibernate)
* **Frontend Framework:** Vaadin Flow (Arquitectura basada en componentes de servidor con comunicación asíncrona y WebSocket/Push)
* **Motor de Base de Datos:** PostgreSQL (Cumplimiento estricto de propiedades ACID e integridad referencial)
* **Gestor de Dependencias:** Maven
* **Control de Versiones:** Git

---

## ⚙️ Configuración e Instalación Local

### Requisitos Previos
* Java Development Kit (JDK) 17 o superior instalado.
* Apache Maven instalado.
* Instancia local de PostgreSQL corriendo.

### Pasos para Ejecutar
1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/tu-usuario/gpiv.git](https://github.com/tu-usuario/gpiv.git)
   cd gpiv
Configurar la Base de Datos:
Modificar el archivo src/main/resources/application.properties con las credenciales de tu PostgreSQL local:

Properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gpiv_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
Compilar y Ejecutar la aplicación:
Mediante Maven:

Bash
mvn spring-boot:run
O directamente ejecutando la clase principal GpivApplication.java desde tu IDE (IntelliJ IDEA).

Acceso a la plataforma:
Abrir el navegador e ingresar a: http://localhost:8080

👥 Integrantes del Equipo
Lara Victor Lionel

Dastres Agulles Valentina

Salazar Agustina

Gambaro Martinez Mariano

Ingeniería de Software — Cursada 2026 Universidad Nacional de Río Negro (UNRN)

---
