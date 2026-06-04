# PersonasService

![.NET](https://img.shields.io/badge/.NET-8.0-512BD4?logo=dotnet&logoColor=white)
![ASP.NET Core](https://img.shields.io/badge/ASP.NET%20Core-Minimal%20API-0C2D48?logo=dotnet&logoColor=white)
![EF Core](https://img.shields.io/badge/Entity%20Framework%20Core-8.0-6DB33F)
![SQL Server](https://img.shields.io/badge/SQL%20Server-Relational%20DB-CC2927?logo=microsoftsqlserver&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)
![Estado](https://img.shields.io/badge/Estado-Ejemplo%20Academico-1F6FEB)
[![Licencia](https://img.shields.io/badge/Licencia-Ver%20LICENSE-lightgrey)](../LICENSE)

Ejemplo de API REST en .NET 8 para gestion de personas, roles y telefonos.

Proyecto de apoyo para practicas del curso Programacion V.

## Tabla de contenido

- [Descripcion](#descripcion)
- [Objetivo academico](#objetivo-academico)
- [Arquitectura de la solucion](#arquitectura-de-la-solucion)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Endpoints principales](#endpoints-principales)
- [Como ejecutar el proyecto](#como-ejecutar-el-proyecto)
- [Documentacion con Swagger](#documentacion-con-swagger)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Notas academicas](#notas-academicas)

## Descripcion

Este proyecto implementa una API REST conectada a SQL Server con operaciones CRUD para:

- Personas
- Roles
- Telefonos

Tambien incluye un endpoint personalizado para crear personas con validacion y logica de negocio.

## Objetivo academico

Este repositorio no busca ser un producto final de produccion.

Se utiliza como referencia didactica para practicar:

- Diseno de APIs REST
- Separacion por capas
- Validacion de datos
- Integracion con base de datos relacional
- Documentacion de servicios con Swagger/OpenAPI

## Arquitectura de la solucion

La solucion fue creada con ASP.NET Core (.NET 8) y organizada por proyectos para separar responsabilidades:

- PersonasService: API principal (Minimal APIs, configuracion, endpoints, Swagger)
- PersonaService.BusinessLogic: reglas de negocio
- PersonaService.Abstract: contratos e interfaces
- PersonasService.DataAccess: acceso a datos con Entity Framework Core
- PersonasService.Models: modelos de apoyo (DTOs y respuestas)

## Tecnologias utilizadas

- .NET 8
- ASP.NET Core Minimal APIs
- Entity Framework Core 8 (SQL Server)
- SQL Server
- Swagger / OpenAPI (Swashbuckle)
- MiniValidation
- Newtonsoft.Json

## Endpoints principales

- /api/Persona
- /api/Rol
- /api/Telefono
- /api/PersonaService

## Como ejecutar el proyecto

1. Crear la base de datos con el script [Creacion de BD.sql](Creaci%C3%B3n%20de%20BD.sql).
2. Revisar la cadena de conexion en [appsettings.json](PersonasService/appsettings.json).
3. Restaurar paquetes y ejecutar la API:

```bash
dotnet restore PersonasService.sln
dotnet run --project PersonasService/PersonasService.csproj
```

## Documentacion con Swagger

Al iniciar la API, Swagger UI queda habilitado en la ruta /swagger del host local.

## Estructura del repositorio

```text
PersonasService/
|- PersonaService.Abstract/
|- PersonaService.BusinessLogic/
|- PersonasService/
|- PersonasService.DataAccess/
|- PersonasService.Models/
|- Creacion de BD.sql
|- PersonasService.sln
```

## Notas academicas

- Este proyecto es un ejemplo educativo para laboratorio y practica guiada.
- Puede ser extendido por estudiantes para agregar autenticacion, pruebas automatizadas y manejo de errores avanzado.
- Para ambientes reales, no almacenar credenciales en archivos de configuracion versionados.
