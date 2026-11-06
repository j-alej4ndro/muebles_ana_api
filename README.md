# Muebles Ana API

API REST para la gestión de una tienda de muebles (**Muebles Ana**), desarrollada con **Spring Boot 4.1.1**, **Java 21**, **Hibernate/JPA** y **MySQL**. El proyecto implementa autenticación con **JWT**, control de acceso por roles y los módulos base de usuarios, productos, proveedores, pedidos a proveedor e inventario/ventas.

## Tabla de contenido

- [Descripción general](#descripción-general)
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Configuración](#configuración)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modelo de datos (esquema de base de datos)](#modelo-de-datos-esquema-de-base-de-datos)
- [Diagrama de base de datos (DrawSQL)](#diagrama-de-base-de-datos-drawsql)
- [Autenticación y autorización](#autenticación-y-autorización)
- [Endpoints principales](#endpoints-principales)
- [Reglas de negocio implementadas](#reglas-de-negocio-implementadas)
- [Manejo de errores](#manejo-de-errores)
- [Documentación Swagger / OpenAPI](#documentación-swagger--openapi)
- [Notas importantes](#notas-importantes)

## Descripción general

El sistema está orientado a cubrir el flujo operativo de una tienda de muebles:

- **Autenticación y usuarios**: registro público de clientes, inicio de sesión con JWT, gestión administrativa de usuarios y roles (`CLIENTE`, `EMPLEADO`, `ADMINISTRADOR`).
- **Catálogo de productos**: CRUD de productos con borrado lógico.
- **Proveedores**: gestión de proveedores con borrado lógico.
- **Pedidos a proveedor (compras)**: creación, recepción y cancelación de pedidos. Al recibir un pedido se generan movimientos de inventario de tipo `ENTRADA_COMPRA`.
- **Inventario**: el stock se calcula a partir de los movimientos registrados (entradas/salidas/ajustes), no como un campo fijo en el producto.
- **Ventas (órdenes de venta)**: creación de órdenes con validación de stock, bloqueo de productos para evitar sobreventa, generación de `SALIDA_VENTA` y soporte para cancelación/entrega según reglas de negocio.

> La base de datos se gestiona con **Hibernate** (`spring.jpa.hibernate.ddl-auto=update`), por lo que **no se utilizan scripts SQL manuales** para crear el esquema.

## Tecnologías

- **Java:** 21
- **Spring Boot:** 4.1.1
- **Spring Web MVC**
- **Spring Data JPA / Hibernate**
- **Spring Security** (stateless, JWT)
- **Bean Validation (Jakarta Validation)**
- **JJWT:** 0.13.0 (generación y validación de tokens)
- **springdoc-openapi (Swagger UI):** 3.1.0
- **Lombok**
- **MySQL** (driver `mysql-connector-j`)
- **Maven** (incluye Maven Wrapper `mvnw`)

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

1. **JDK 21** (recomendado: Temurin/OpenJDK 21).
2. **Maven 3.9+** (opcional, porque el proyecto incluye `./mvnw`).
3. **MySQL 8.x** en ejecución.
4. **Git** (opcional, para clonar el repositorio).
5. Un cliente HTTP o navegador para probar la API (por ejemplo: **Swagger UI**, Postman, Insomnia o curl).

Verificación rápida:

```bash
java -version
./mvnw -version
mysql --version
```

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd muebles_ana_api
```

### 2. Crear la base de datos en MySQL

Crea una base de datos vacía (Hibernate generará/actualizará las tablas):

```sql
CREATE DATABASE muebles_ana_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> Puedes usar otro nombre, pero debes actualizar la URL en `src/main/resources/application.yml`.

### 3. Configurar credenciales y propiedades

Edita `src/main/resources/application.yml` con tus valores locales:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/muebles_ana_db
    username: TU_USUARIO
    password: TU_CONTRASENA
```

También revisa la sección `jwt` y `app.admin` (ver [Configuración](#configuración)).

### 4. Compilar el proyecto

```bash
./mvnw clean compile
```

### 5. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

O generar el JAR y ejecutarlo:

```bash
./mvnw clean package -DskipTests
java -jar target/muebles_ana_api-0.0.1-SNAPSHOT.jar
```

La API quedará disponible (por defecto) en:

- **Base URL:** `http://localhost:8080/api-muebles`
- **Swagger UI:** `http://localhost:8080/api-muebles/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/api-muebles/api-docs`

## Configuración

Archivo principal: `src/main/resources/application.yml`

### Propiedades relevantes

| Propiedad | Descripción | Valor por defecto / ejemplo |
|---|---|---|
| `server.port` | Puerto de la aplicación | `8080` |
| `server.servlet.context-path` | Prefijo de la API | `/api-muebles` |
| `spring.datasource.url` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/muebles_ana_db` |
| `spring.datasource.username` | Usuario MySQL | `root` |
| `spring.datasource.password` | Contraseña MySQL | *(configurar localmente)* |
| `spring.jpa.hibernate.ddl-auto` | Estrategia de esquema con Hibernate | `update` |
| `spring.jpa.open-in-view` | Open Session in View | *(según configuración actual; recomendado `false` en producción)* |
| `jwt.secret` | Secreto Base64 para firmar tokens (HS256) | *(configurar; cambiar en producción)* |
| `jwt.expiration` | Expiración del token en milisegundos | `36000000` (10 horas) |
| `app.admin.nombre` | Nombre del admin inicial (bootstrap) | `Administrador` |
| `app.admin.correo` | Correo del admin inicial | `admin@mueblesana.com` |
| `app.admin.password` | Contraseña del admin inicial | `Admin1234` |

### Bootstrap del primer administrador

Al iniciar la aplicación, un `CommandLineRunner` verifica si existe al menos un usuario con rol `ADMINISTRADOR` activo:

- Si **no existe ninguno**, crea (o promueve, según la lógica implementada) un administrador usando los valores de `app.admin.*`.
- Si **ya existe al menos uno**, no realiza cambios.

Esto permite contar con un acceso inicial sin ejecutar SQL manual.

## Estructura del proyecto

El proyecto sigue una organización por capas (estilo Spring Boot clásico):

```text
src/main/java/com/unicartagena/muebles_ana_api/
├── MueblesAnaApiApplication.java
├── application/
│   ├── controller/        # Controladores REST
│   ├── dto/               # DTOs de respuesta
│   ├── entities/          # Entidades JPA
│   ├── repositories/      # Repositorios Spring Data JPA
│   ├── request/           # DTOs de entrada / requests
│   └── service/
│       ├── *.java         # Interfaces de servicio
│       └── impl/          # Implementaciones de servicio
└── base/
    ├── configs/           # Seguridad, JWT filter, bootstrap admin, swagger, password encoder
    ├── exceptions/        # Excepciones personalizadas y GlobalExceptionHandler
    └── utils/             # Utilidades (JWT, converters, etc.)
```

## Modelo de datos (esquema de base de datos)

Hibernate genera/actualiza el esquema a partir de las entidades JPA. Las entidades principales del dominio son:

| Entidad (Java) | Tabla | Descripción |
|---|---|---|
| `Usuario` | `usuario` | Persona autenticada: `CLIENTE`, `EMPLEADO` o `ADMINISTRADOR` |
| `Producto` | `producto` | Artículo que se compra y se vende |
| `Proveedor` | `proveedor` | Proveedor de mercancía |
| `PedidoProveedor` | `pedido_proveedor` | Cabecera de compra a proveedor |
| `DetallePedidoProveedor` | `detalle_pedido_proveedor` | Líneas del pedido a proveedor |
| `OrdenVenta` | `orden_venta` | Cabecera de venta |
| `DetalleOrdenVenta` | `detalle_orden_venta` | Líneas de la orden de venta |
| `MovimientoInventario` | `movimiento_inventario` | Registro de entradas/salidas; base para calcular stock |

### Relaciones principales

- `Usuario` 1..N `OrdenVenta`
- `OrdenVenta` 1..N `DetalleOrdenVenta`
- `Producto` 1..N `DetalleOrdenVenta`
- `Usuario` 1..N `PedidoProveedor`
- `Proveedor` 1..N `PedidoProveedor`
- `PedidoProveedor` 1..N `DetallePedidoProveedor`
- `Producto` 1..N `DetallePedidoProveedor`
- `Producto` 1..N `MovimientoInventario`
- `Usuario` 1..N `MovimientoInventario`
- `PedidoProveedor` 0..N `MovimientoInventario` (entradas por compra)
- `OrdenVenta` 0..N `MovimientoInventario` (salidas/anulaciones por venta, según implementación)

### Enums de dominio

| Enum | Valores | Uso |
|---|---|---|
| `Rol` | `CLIENTE`, `EMPLEADO`, `ADMINISTRADOR` | Rol de `Usuario` |
| `EstadoPedido` | `PENDIENTE`, `RECIBIDO`, `CANCELADO` | Estado de `PedidoProveedor` |
| `EstadoOrden` | `PENDIENTE`, `ENTREGADA`, `CANCELADA` | Estado de `OrdenVenta` |
| `TipoMovimiento` | `ENTRADA_COMPRA`, `SALIDA_VENTA`, `ANULACION_VENTA`, `AJUSTE` | Tipo de `MovimientoInventario` |

## Diagrama de base de datos (DrawSQL)

Puedes visualizar el esquema de la base de datos en DrawSQL aquí:

- **DrawSQL:** [Muebles Ana - Diagrama de BD](https://drawsql.app/teams/josealejandro/diagrams/muebles-ana)

## Autenticación y autorización

La API usa **JWT (JSON Web Token)** con sesiones **stateless**:

1. El cliente se registra con `POST /auth/register` o inicia sesión con `POST /auth/login`.
2. El backend responde con un token JWT (y datos básicos del usuario en el login).
3. El cliente envía el token en cada petición protegida:

```http
Authorization: Bearer <TOKEN_JWT>
```

### Roles

| Rol | Descripción | Autoridad Spring Security |
|---|---|---|
| `CLIENTE` | Usuario comprador/autoregistrado | `ROLE_CLIENTE` |
| `EMPLEADO` | Gestión operativa (productos, proveedores, compras, entregas, etc.) | `ROLE_EMPLEADO` |
| `ADMINISTRADOR` | Gestión administrativa total (usuarios, roles, estado) | `ROLE_ADMINISTRADOR` |

### Reglas de acceso (resumen)

- **Público:** `/auth/**`, Swagger/OpenAPI, `GET /productos/activos`.
- **Autenticado:** `/usuarios/me` y operaciones que requieran usuario logueado (por ejemplo, crear/ver/cancelar órdenes según reglas de negocio).
- **EMPLEADO o ADMINISTRADOR:** `/productos/**` (gestión), `/proveedores/**`, `/pedidos-proveedor/**`.
- **Solo ADMINISTRADOR:** `/usuarios/**` (excepto `/usuarios/me`, que es para el perfil propio).

> El token incluye el rol en su claim. Si cambias el rol o estado de un usuario, puede ser necesario **volver a iniciar sesión** para obtener un token actualizado.

## Endpoints principales

> Todas las rutas están bajo el contexto `/api-muebles` (por ejemplo: `POST /api-muebles/auth/login`).

### Autenticación

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Registra un nuevo usuario con rol `CLIENTE` |
| `POST` | `/auth/login` | Público | Autentica y devuelve JWT |

### Usuarios

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/usuarios/me` | Autenticado | Obtiene el perfil del usuario autenticado |
| `PUT` | `/usuarios/me` | Autenticado | Actualiza el perfil propio (sin cambiar rol/estado) |
| `GET` | `/usuarios` | ADMIN | Lista usuarios |
| `GET` | `/usuarios/{id}` | ADMIN | Obtiene un usuario por ID |
| `POST` | `/usuarios` | ADMIN | Crea usuario con rol (`CLIENTE`, `EMPLEADO`, `ADMINISTRADOR`) |
| `PUT` | `/usuarios/{id}` | ADMIN | Actualiza datos del usuario (sin rol/estado) |
| `PATCH` | `/usuarios/{id}/rol` | ADMIN | Cambia el rol |
| `PATCH` | `/usuarios/{id}/estado` | ADMIN | Activa/desactiva el usuario |
| `DELETE` | `/usuarios/{id}` | ADMIN | Borrado lógico (desactivación) |

### Productos

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/productos/activos` | Público | Lista productos activos |
| `GET` | `/productos` | EMPLEADO/ADMIN | Lista productos |
| `GET` | `/productos/{id}` | EMPLEADO/ADMIN | Obtiene producto por ID |
| `POST` | `/productos` | EMPLEADO/ADMIN | Crea producto (activo por defecto) |
| `PUT` | `/productos/{id}` | EMPLEADO/ADMIN | Actualiza producto |
| `DELETE` | `/productos/{id}` | EMPLEADO/ADMIN | Borrado lógico |

### Proveedores

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/proveedores` | EMPLEADO/ADMIN | Lista proveedores |
| `GET` | `/proveedores/{id}` | EMPLEADO/ADMIN | Obtiene proveedor por ID |
| `POST` | `/proveedores` | EMPLEADO/ADMIN | Crea proveedor (activo por defecto) |
| `PUT` | `/proveedores/{id}` | EMPLEADO/ADMIN | Actualiza proveedor |
| `DELETE` | `/proveedores/{id}` | EMPLEADO/ADMIN | Borrado lógico |

### Pedidos a proveedor (compras)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/pedidos-proveedor` | EMPLEADO/ADMIN | Crea pedido a proveedor |
| `GET` | `/pedidos-proveedor` | EMPLEADO/ADMIN | Lista pedidos |
| `GET` | `/pedidos-proveedor/{id}` | EMPLEADO/ADMIN | Obtiene pedido por ID |
| `POST` | `/pedidos-proveedor/{id}/recibir` | EMPLEADO/ADMIN | Recibe pedido y genera entradas de inventario |
| `POST` | `/pedidos-proveedor/{id}/cancelar` | EMPLEADO/ADMIN | Cancela pedido (si está pendiente) |

### Órdenes de venta

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/ordenes-venta` | Autenticado | Crea orden de venta (valida stock y descuenta inventario) |
| `GET` | `/ordenes-venta` | Autenticado | Lista órdenes (según reglas: propias o todas para EMPLEADO/ADMIN) |
| `GET` | `/ordenes-venta/{id}` | Autenticado | Obtiene orden (dueño o EMPLEADO/ADMIN) |
| `POST` | `/ordenes-venta/{id}/cancelar` | Autenticado | Cancela orden pendiente y repone inventario |
| `POST` | `/ordenes-venta/{id}/entregar` | EMPLEADO/ADMIN | Marca la orden como entregada |

> Los nombres exactos de campos en requests/responses pueden consultarse en Swagger UI.

## Reglas de negocio implementadas

- **Borrado lógico:** Usuarios, productos y proveedores no se eliminan físicamente; se desactivan (`activo=false`) o se gestionan por estado según el caso.
- **Stock por movimientos:** El stock se calcula como la suma de `movimiento_inventario.cantidad` por producto (entradas positivas, salidas negativas).
- **Precio congelado en ventas:** El `precioUnitario` del detalle de venta se toma del producto en el momento de crear la orden.
- **Control de sobreventa:** Al crear una orden se bloquean productos y se valida stock disponible antes de confirmar.
- **Recepción de compras:** Recibir un `PedidoProveedor` genera movimientos `ENTRADA_COMPRA` y cambia el estado a `RECIBIDO` en una transacción.
- **Cancelación de ventas:** Cancelar una orden pendiente genera movimientos `ANULACION_VENTA` para reponer stock.
- **Protección de administración:** No se permite desactivar o degradar al último administrador activo.
- **Seguridad del perfil propio:** `/usuarios/me` no permite modificar `rol` ni `activo`.

## Manejo de errores

La API utiliza un `GlobalExceptionHandler` (`@RestControllerAdvice`) para devolver respuestas consistentes. Ejemplos de códigos HTTP:

| Situación | Código HTTP |
|---|---|
| Recurso no encontrado | `404 Not Found` |
| Datos inválidos / validaciones | `400 Bad Request` |
| Conflicto de negocio (duplicados, stock insuficiente, etc.) | `409 Conflict` |
| Sin permisos | `403 Forbidden` |
| Error interno no controlado | `500 Internal Server Error` |

## Documentación Swagger / OpenAPI

Con la aplicación en ejecución:

- **Swagger UI:** `http://localhost:8080/api-muebles/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/api-muebles/api-docs`

### Cómo probar endpoints protegidos en Swagger

1. Ejecuta `POST /auth/login` con un usuario válido.
2. Copia el `token` de la respuesta.
3. Haz clic en **Authorize** (Swagger UI).
4. Ingresa: `Bearer <TU_TOKEN>`.
5. Prueba los endpoints según el rol del usuario.

## Notas importantes

- **Seguridad:** Cambia `jwt.secret` y las credenciales de `app.admin` antes de usar el proyecto en un entorno real. No subas secretos reales al repositorio.
- **Hibernate y esquema:** `ddl-auto=update` es útil para desarrollo, pero en producción se recomienda usar migraciones controladas (Flyway/Liquibase) y validación de esquema.
- **Tokens y cambios de rol:** El rol viaja dentro del JWT. Si un administrador cambia tu rol, debes iniciar sesión nuevamente para que el nuevo token refleje el cambio.
- **Swagger y defaults visuales:** Algunos campos (como enums) pueden mostrarse preseleccionados en Swagger UI por comportamiento de la interfaz; esto no cambia las reglas de autorización ni la validación del backend.
- **Tablas antiguas:** Si venías de versiones anteriores del modelo, pueden quedar tablas antiguas en MySQL (por ejemplo `productos`, `tipo_documentos`). Hibernate no las elimina automáticamente.
