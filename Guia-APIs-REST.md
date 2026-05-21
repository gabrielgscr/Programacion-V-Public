# Guía para la elaboración de APIs REST y buenas prácticas

## Propósito

Esta guía resume criterios técnicos para diseñar, implementar y documentar APIs alineadas con el estilo REST. El objetivo no es solo exponer endpoints, sino construir interfaces consistentes, predecibles, seguras y fáciles de mantener.

## ¿Qué significa cumplir con REST?

REST es un estilo de arquitectura para sistemas distribuidos basado en recursos, una interfaz uniforme y comunicación sin estado. Una API orientada a REST debe procurar:

- Identificar cada recurso mediante una URL clara.
- Usar correctamente los métodos HTTP.
- Mantener cada solicitud independiente, sin depender de estado almacenado en el servidor entre peticiones.
- Representar recursos en formatos estándar, normalmente JSON.
- Aprovechar los códigos de estado HTTP para comunicar el resultado de cada operación.
- Facilitar caché, escalabilidad y evolución controlada de la interfaz.

Cumplir con REST no consiste únicamente en usar JSON y HTTP; también implica diseñar la API como una colección coherente de recursos y comportamientos.

## Restricciones REST que conviene explicitar

Si se quiere hablar con mayor precisión de una API RESTful, conviene contrastarla con las restricciones clásicas de REST:

- Cliente-servidor: separación clara entre consumidor y proveedor del servicio.
- Sin estado: cada petición debe ser autosuficiente.
- Almacenable en caché: las respuestas deben indicar cuándo pueden almacenarse y reutilizarse.
- Interfaz uniforme: recursos identificables, mensajes autodescriptivos, representaciones consistentes y semántica HTTP bien aplicada.
- Sistema por capas: el cliente no necesita conocer si interactúa con el servicio final, un proxy o una pasarela.
- Código bajo demanda: es opcional y poco común en APIs modernas.

En términos estrictos, una API plenamente RESTful también debería considerar hipermedia como motor del estado de la aplicación, es decir, incluir enlaces o acciones relacionadas dentro de las respuestas para guiar la navegación del cliente. En la práctica, muchas APIs académicas y empresariales siguen solo una parte de estas restricciones; en esos casos suele ser más preciso decir que son APIs HTTP inspiradas en REST o APIs con estilo REST.

## Principios base de diseño

### 1. Modelar recursos, no acciones

La API debe girar alrededor de sustantivos que representen entidades del dominio.

Correcto:

- GET /usuarios
- GET /usuarios/15
- POST /usuarios

Evitar:

- GET /obtenerUsuarios
- POST /crearUsuario
- POST /eliminarUsuario

El verbo principal ya lo aporta el método HTTP. La URL debe describir el recurso.

### 2. Usar métodos HTTP de forma semántica

Los métodos más comunes deben respetar su intención:

- GET: consultar recursos sin modificar estado.
- POST: crear recursos o ejecutar operaciones no idempotentes.
- PUT: reemplazar completamente un recurso existente.
- PATCH: actualizar parcialmente un recurso.
- DELETE: eliminar un recurso.

Una mala práctica frecuente es usar POST para todo. Eso rompe la semántica HTTP, dificulta el uso de caché y vuelve menos clara la API.

### 3. Mantener ausencia de estado

Cada solicitud debe contener toda la información necesaria para ser procesada. La autenticación, autorización, filtros y contexto deben viajar en la petición.

Evitar depender de sesiones implícitas del lado servidor cuando el objetivo es una API REST pública o integrable.

### 4. Diseñar URLs consistentes

Buenas prácticas para las rutas:

- Usar sustantivos en plural cuando represente colecciones: /productos, /clientes, /pedidos.
- Mantener nombres cortos y claros.
- Usar minúsculas y guiones si hace falta legibilidad.
- Evitar mezclar estilos en la misma API.
- Reflejar jerarquías reales solo cuando exista relación fuerte entre recursos.

Ejemplos:

- GET /pedidos
- GET /pedidos/120
- GET /pedidos/120/detalles
- GET /clientes/44/pedidos

### 5. Representar correctamente el resultado con HTTP

El resultado de una operación debe comunicarse con el código HTTP correcto, no solamente con un campo interno como success = true.

## Códigos de estado recomendados

### Respuestas exitosas

- 200 OK: consulta o actualización exitosa.
- 201 Created: recurso creado correctamente.
- 202 Accepted: solicitud aceptada para procesamiento posterior.
- 204 No Content: operación exitosa sin cuerpo de respuesta, común en DELETE o algunos PUT/PATCH.

### Errores del cliente

- 400 Bad Request: solicitud mal construida.
- 401 Unauthorized: falta autenticación válida.
- 403 Forbidden: autenticado, pero sin permisos suficientes.
- 404 Not Found: recurso inexistente.
- 409 Conflict: conflicto de estado, por ejemplo duplicidad.
- 422 Unprocessable Entity: datos válidos en formato, pero inválidos en reglas de negocio o validación.

### Errores del servidor

- 500 Internal Server Error: fallo inesperado.
- 502 Bad Gateway: respuesta inválida recibida desde un servicio externo o dependencia intermedia.
- 503 Service Unavailable: servicio temporalmente no disponible.

## Convención recomendada para respuestas JSON

Una API consistente debe responder con estructuras previsibles. Por ejemplo:

### Respuesta exitosa

```json
{
  "data": {
    "id": 15,
    "nombre": "Ana",
    "correo": "ana@correo.com"
  }
}
```

Si se busca un diseño más estrictamente RESTful, la respuesta puede incluir enlaces relacionados para facilitar la navegación del cliente:

```json
{
  "data": {
    "id": 15,
    "nombre": "Ana",
    "correo": "ana@correo.com"
  },
  "links": {
    "self": "/usuarios/15",
    "pedidos": "/usuarios/15/pedidos"
  }
}
```

### Respuesta de colección con paginación

```json
{
  "data": [
    {
      "id": 1,
      "nombre": "Producto A"
    },
    {
      "id": 2,
      "nombre": "Producto B"
    }
  ],
  "meta": {
    "page": 1,
    "pageSize": 10,
    "total": 35,
    "totalPages": 4
  }
}
```

### Respuesta de error

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Los datos enviados no son válidos.",
    "details": [
      {
        "field": "correo",
        "message": "El correo ya existe."
      }
    ]
  }
}
```

Lo importante no es una estructura única universal, sino mantener una convención estable en toda la API.

## Buenas prácticas clave

### Nombres claros y consistentes

- Mantener el mismo criterio de nombres en rutas, atributos y parámetros.
- Evitar abreviaturas ambiguas.
- No mezclar español e inglés sin una razón definida por el proyecto.

### Validación de entrada

Toda API debe validar:

- Tipos de datos.
- Campos requeridos.
- Rangos permitidos.
- Formatos, por ejemplo correo, fecha o teléfono.
- Reglas de negocio, por ejemplo unicidad o estados válidos.

Nunca se debe confiar en que el cliente enviará información correcta.

### Manejo uniforme de errores

Los errores deben ser comprensibles para quien consume la API.

Evitar:

- Mensajes genéricos sin contexto.
- Exponer trazas internas de la aplicación.
- Responder siempre con 200 aunque exista error lógico.

Preferir:

- Códigos HTTP adecuados.
- Mensajes claros.
- Identificadores de error reutilizables.
- Detalles de validación por campo cuando sea necesario.

### Paginación, filtros y ordenamiento

Cuando una colección puede crecer, no conviene devolver todos los elementos de una sola vez.

Conviene soportar parámetros como:

- page
- pageSize
- sort
- order
- filtros o campos específicos como estado, fecha, categoría

Ejemplo:

- GET /productos?page=2&pageSize=20&sort=nombre&order=asc

### Versionado de la API

Una API evoluciona. Para evitar romper clientes existentes, se recomienda versionar.

Una estrategia común es incluir la versión en la ruta:

- /api/v1/usuarios
- /api/v2/usuarios

También es válido versionar por encabezados, pero en contextos académicos y de integración básica la versión en la URL suele ser más simple de entender y mantener.

### Idempotencia cuando aplica

Una operación idempotente produce el mismo resultado observable aunque se ejecute varias veces con la misma entrada.

- GET debe ser idempotente.
- PUT y DELETE deberían comportarse como idempotentes.
- POST normalmente no es idempotente.

Entender esto ayuda a diseñar integraciones más robustas y reintentos seguros.

### Seguridad desde el diseño

Buenas prácticas mínimas:

- Usar HTTPS.
- Proteger endpoints sensibles con autenticación y autorización.
- Validar y sanear entradas.
- Limitar intentos o aplicar rate limiting cuando corresponda.
- No exponer datos sensibles innecesarios.
- Registrar eventos relevantes sin almacenar secretos en logs.

### Documentación obligatoria

Una API sin documentación obliga al consumidor a adivinar su comportamiento. La documentación debe incluir:

- Base URL.
- Recursos disponibles.
- Métodos soportados.
- Parámetros de ruta, query y body.
- Ejemplos de solicitud y respuesta.
- Códigos HTTP posibles.
- Reglas de autenticación.
- Casos de error.

Cuando sea posible, usar OpenAPI o Swagger para formalizar el contrato.

## Recomendaciones de estructura para endpoints

### Colecciones

- GET /usuarios
- POST /usuarios

### Recursos individuales

- GET /usuarios/{id}
- PUT /usuarios/{id}
- PATCH /usuarios/{id}
- DELETE /usuarios/{id}

### Subrecursos

- GET /usuarios/{id}/pedidos
- POST /usuarios/{id}/pedidos

Solo deben existir subrecursos cuando la relación tenga sentido dentro del dominio.

## Errores comunes que deben evitarse

- Diseñar rutas orientadas a verbos en lugar de recursos.
- Usar POST para consultas, actualizaciones y eliminaciones sin justificación.
- Devolver siempre 200 OK.
- No validar datos de entrada.
- Exponer excepciones internas al cliente.
- No versionar la API.
- Devolver estructuras JSON distintas para casos similares.
- Crear endpoints demasiado acoplados al frontend actual.
- Ignorar paginación en colecciones grandes.
- No documentar cambios de contrato.

## Checklist de calidad para una API REST

Antes de publicar una API, conviene verificar:

- Las rutas representan recursos del dominio.
- Los métodos HTTP son coherentes con la operación.
- Los códigos de estado están bien aplicados.
- Las respuestas JSON siguen una convención uniforme.
- Las validaciones cubren formato y reglas de negocio.
- Los errores son claros y no exponen información sensible.
- Existe autenticación y autorización donde corresponde.
- La API está versionada.
- Los endpoints de lista tienen paginación, filtros y ordenamiento cuando aplica.
- La documentación está actualizada.
- Hay pruebas para casos exitosos, inválidos y de error.

## Ejemplo breve de diseño coherente

Recurso: productos

- GET /api/v1/productos
  Lista productos con paginación y filtros.
- GET /api/v1/productos/10
  Obtiene el producto con id 10.
- POST /api/v1/productos
  Crea un nuevo producto.
- PATCH /api/v1/productos/10
  Actualiza parcialmente el producto.
- DELETE /api/v1/productos/10
  Elimina el producto.

Ejemplo de creación:

```http
POST /api/v1/productos
Content-Type: application/json
```

```json
{
  "nombre": "Teclado mecánico",
  "precio": 32500,
  "stock": 8
}
```

Respuesta esperada:

```http
201 Created
Location: /api/v1/productos/101
```

```json
{
  "data": {
    "id": 101,
    "nombre": "Teclado mecánico",
    "precio": 32500,
    "stock": 8
  }
}
```

## Conclusión

Diseñar una API REST de calidad implica tomar decisiones consistentes sobre recursos, métodos HTTP, códigos de respuesta, validación, seguridad, documentación y evolución del contrato. Una API bien diseñada reduce errores de integración, facilita el mantenimiento y mejora la experiencia de quienes la consumen.

Como criterio práctico: si una API es fácil de entender sin explicaciones verbales adicionales, devuelve respuestas predecibles y respeta la semántica HTTP, va por buen camino.