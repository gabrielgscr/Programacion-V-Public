# .NET y su uso para APIs REST

![.NET](https://img.shields.io/badge/.NET-8.0-512BD4?logo=dotnet&logoColor=white)
![ASP.NET Core](https://img.shields.io/badge/ASP.NET%20Core-Minimal%20API-0C2D48?logo=dotnet&logoColor=white)
![EF Core](https://img.shields.io/badge/Entity%20Framework%20Core-8.0-6DB33F)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)
![Academico](https://img.shields.io/badge/Contexto-Ejemplo%20Academico-1F6FEB)

Esta referencia breve resume por que .NET es una opcion solida para construir APIs REST en entornos academicos y profesionales.

## Que es .NET en este contexto

.NET es una plataforma de desarrollo de Microsoft que permite crear aplicaciones backend modernas, incluyendo APIs web con ASP.NET Core.

## Por que usar .NET para APIs

- Alto rendimiento para servicios HTTP.
- Soporte multiplataforma (Windows, Linux, macOS).
- Tipado fuerte y buena mantenibilidad del codigo.
- Ecosistema robusto para autenticacion, logging, pruebas y despliegue.
- Integracion nativa con OpenAPI/Swagger para documentar endpoints.

## Componentes comunes para una API en .NET

- ASP.NET Core: framework web para exponer endpoints HTTP.
- Minimal APIs o Controllers: dos estilos para definir rutas y operaciones.
- Entity Framework Core: acceso a base de datos relacional.
- Inyeccion de dependencias: organizacion y desacoplamiento de capas.
- appsettings.json: configuracion centralizada (cadena de conexion, variables, etc.).

## Flujo basico de trabajo

1. Definir modelos y contratos.
2. Configurar servicios (DB, dependencias, CORS, Swagger).
3. Crear endpoints (GET, POST, PUT, DELETE).
4. Validar entradas y manejar errores.
5. Probar con Swagger o cliente HTTP.

## Ejemplo en este repositorio

Para ver una implementacion completa, revisa:

- [README de PersonasService](README.md)
- [Proyecto Web API](PersonasService/)

## Comandos utiles

```bash
dotnet restore PersonasService.sln
dotnet run --project PersonasService/PersonasService.csproj
```
