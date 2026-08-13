# Mantenimiento de personas para Android

Aplicación Android nativa escrita en Kotlin que permite consultar, crear, editar y eliminar personas mediante el microservicio `EjemploMicroServicioPersona` del curso. La interfaz está construida con Jetpack Compose y consume la API REST con Retrofit.

## Cómo fue creada

1. Se creó un proyecto Android vacío en Android Studio, con Kotlin y la plantilla Jetpack Compose.
2. Se sustituyó la pantalla de muestra por un mantenimiento CRUD de personas.
3. Se agregó Retrofit y Gson para comunicarse con el endpoint `/api/Persona`.
4. Se separó el código en capas para evitar que la interfaz conozca detalles HTTP o SQL.

## Estructura

- `core/network`: resultados de red comunes.
- `data/remote`: contrato Retrofit y DTO que representan el JSON del API.
- `data/repository`: implementación de acceso remoto y mapeos entre DTO y dominio.
- `domain`: entidad `Persona` y contrato del repositorio.
- `di`: creación centralizada de dependencias.
- `ui/components`: controles Compose reutilizables.
- `ui/people`: listado, formulario, diálogo de borrado y `ViewModel`.
- `settings`: preferencias persistentes de idioma y tema.
- `ui/theme`: paletas Material 3 azul/blanco para los modos claro y oscuro.
- `res/values` y `res/values-es`: textos inglés y español. Android selecciona el idioma según la configuración del dispositivo.

Cada clase incluye un comentario inicial con su responsabilidad.

## Configuración del servicio

La URL base se declara en `app/build.gradle.kts` como `PERSONA_API_BASE_URL`. Por defecto apunta al microservicio local ejecutado en el equipo anfitrión:

```kotlin
https://10.0.2.2:7231/
```

En el emulador Android, `10.0.2.2` representa el equipo anfitrión; por ello la ruta efectiva es `https://10.0.2.2:7231/api/Persona`. En depuración, la aplicación acepta únicamente el certificado de desarrollo local para esta dirección. En un teléfono físico, use la IP LAN del equipo en vez de `localhost` y configure un certificado válido para ella.

Para producción, utilice siempre HTTPS. La autorización de tráfico HTTP solo está en el manifiesto de la variante `debug`, para facilitar la práctica local; no forma parte del APK de publicación.

## Compilar y ejecutar

Requisitos: Android Studio con Android SDK Platform 37 instalado, JDK 11 y un dispositivo Android 10 (API 29) o superior, físico o emulador.

1. Abra la carpeta `EjemploNativoP5` en Android Studio.
2. Sincronice Gradle para descargar las dependencias.
3. Verifique la URL base del API y que el servicio esté disponible.
4. Seleccione un dispositivo y ejecute la configuración `app`.

También puede compilar desde una terminal de Windows:

```powershell
cd EjemploNativoP5
.\gradlew.bat assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/`.

## Funcionalidad

- Lista las personas con `GET /api/Persona`.
- Crea con `POST /api/Persona`; la contraseña se solicita únicamente en esta operación.
- Edita con `PUT /api/Persona/{id}`.
- Elimina con confirmación mediante `DELETE /api/Persona/{id}`.
- Muestra estados de carga, vacío y error recuperable.
- Usa idioma español o inglés según el sistema y tema claro u oscuro según la preferencia del dispositivo.
- Pagina el listado con `GET /api/Persona/page` y permite buscar una identificación exacta con `GET /api/Persona/{id}`.

No se agregaron pruebas unitarias al proyecto.
