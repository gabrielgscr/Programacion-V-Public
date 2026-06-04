# Tarea Corta 2 – Investigación Gateways

**Profesor:** Prof. Ing. Gabriel González Solano  
**Curso:** Programación V  
**Institución:** Colegio Universitario de Cartago

---

## Objetivo general

Comprender qué es un **API Gateway** desde sus conceptos, beneficios, riesgos y patrones, y demostrarlo mediante la implementación de microservicios consumidos a través de un Gateway configurado por el equipo.

---

## Consigna

- Explicar el rol de un Gateway en una arquitectura de microservicios.
- Diseñar e implementar **2–3 microservicios** con responsabilidades claras.
- Configurar un **API Gateway** que exponga una URL única para consumir esos microservicios.
- Demostrar capacidades típicas de Gateway: enrutamiento, agregación simple, seguridad básica, transformaciones o políticas, según la herramienta seleccionada.
- Documentar y justificar decisiones técnicas, incluyendo sus consecuencias.

---

## Requerimientos del trabajo

## Parte teórica – Investigación

Entregar un documento en formato **PDF** con contenido propio y referencias. Debe incluir:

1. Definición de API Gateway y su propósito.
2. Problemas que resuelve y por qué no basta con “un reverse proxy” en algunos casos.
3. Funciones comunes del Gateway, mínimo 8. Por ejemplo:
   - Routing / reverse proxy.
   - Authentication / authorization.
   - Rate limiting / throttling.
   - Caching.
   - SSL termination.
   - Load balancing.
   - Request / response transformation.
   - Aggregation, Backend for Frontend.
   - Observabilidad: logs, tracing, metrics.
   - Circuit breaking / resiliencia, si la herramienta lo soporta.
4. Ventajas y desventajas: puntos fuertes y riesgos, como *single point of failure*, acoplamiento, latencia, complejidad, entre otros.
5. Patrones relacionados:
   - BFF.
   - Strangler Fig, si aplica.
   - API Composition.
   - Service Discovery, como mención.
6. Comparación breve, en tabla, de al menos 2 herramientas de Gateway. Ejemplos:
   - Ocelot.
   - Kong.
   - NGINX.
   - Traefik.
   - YARP.
   - Spring Cloud Gateway.

   La comparación debe indicar:

   | Criterio | Descripción |
   |---|---|
   | Lenguaje / stack | Tecnología o entorno principal de la herramienta. |
   | Tipo | Open-source / enterprise. |
   | Curva de aprendizaje | Nivel de dificultad para aprender y configurar. |
   | Funciones clave | Capacidades principales que ofrece. |
   | Caso ideal de uso | Escenario donde conviene utilizarla. |

7. Conclusión: cuándo usar un Gateway y cuándo evitarlo.

---

## Parte práctica

El equipo implementará un sistema mínimo de microservicios y lo expondrá mediante un Gateway.

---

## Microservicios mínimos

Se deben implementar al menos **2 microservicios**, idealmente **3**.

Ejemplos sugeridos, aunque el equipo puede proponer otros:

### Servicio A: Catálogo / Productos

- CRUD básico o al menos endpoints `GET` y `POST`.

### Servicio B: Órdenes / Pedidos

- Crear pedido.
- Listar pedidos.

### Servicio C opcional: Usuarios / Clientes

- Registrar clientes.
- Listar clientes.

---

## Reglas mínimas de los microservicios

- Cada microservicio debe tener su propia API REST.
- Cada microservicio debe tener persistencia en base de datos.
- Se debe utilizar una base de datos **No SQL**.
- Cada microservicio debe correr en un puerto distinto.
- Cada microservicio debe ser ejecutable de forma independiente.
- Debe existir al menos una comunicación inter-servicio. Por ejemplo: Órdenes consulta Productos o Clientes.
- Debe respetarse el estándar RESTful.

---

## Requerimientos obligatorios del Gateway

Configurar un API Gateway que cumpla como mínimo con lo siguiente:

### 1. Entrada única

Debe existir una URL, host o puerto único para el consumidor o cliente.

### 2. Routing

El Gateway debe enrutar las solicitudes hacia los microservicios correspondientes.

| Ruta en Gateway | Servicio destino |
|---|---|
| `/api/productos/*` | Servicio Productos |
| `/api/ordenes/*` | Servicio Órdenes |
| `/api/clientes/*` | Servicio Clientes, si existe |

### 3. Política adicional

Se deben implementar mínimo **2** de las siguientes opciones:

- Transformación de ruta o encabezados, por ejemplo: `rewrite`, agregar o eliminar headers.
- Agregación: endpoint en Gateway que combine datos. Por ejemplo: `/api/resumen-orden/{id}` trae orden y detalle de productos.

### 4. Observabilidad mínima

Debe evidenciarse:

- Logs del Gateway mostrando solicitudes enrutadas.
- Logs de cada microservicio mostrando recepción y respuesta.

> **Nota:** Si la herramienta escogida no soporta alguna funcionalidad, el equipo debe justificarlo y proponer un equivalente.

---

## Entregables

1. Documento formal de la parte teórica:
   - Portada.
   - Introducción.
   - Desarrollo con las secciones solicitadas.
   - Conclusiones y recomendaciones.
   - Bibliografía en formato **APA obligatorio**.

   > Si no se incluyen referencias a fuentes formales, el trabajo se considera no entregado.

2. Ruta de repositorio Git con fuentes de los microservicios y Gateway.
   - Debe incluir un `README` detallado para instalación y ejecución.

3. Documento de Excel con la división de responsabilidad de cada miembro del equipo.

4. Evidencias de funcionamiento:
   - Video corto, mínimo de 5 minutos, mostrando:
     - Servicios en ejecución.
     - Gateway funcional.
     - Pruebas de funcionalidad en Postman de las distintas operaciones.
   - Documento con pantallazos como evidencia de la ejecución.

---

## Criterios de evaluación

| Criterio | Porcentaje |
|---|---:|
| Documento teórico | 30% |
| Diseño y microservicios | 30% |
| Gateway | 30% |
| Calidad de los entregables | 10% |
| **Total** | **100%** |

---

## Aspectos administrativos

1. Debe realizarse en los equipos de trabajo definidos.
2. Fecha de entrega: **18 de junio de 2026**.
3. La valoración es individual, basada en lo indicado en el documento de responsabilidades.
