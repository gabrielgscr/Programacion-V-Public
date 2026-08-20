# Personas CUC — Flutter

Cliente Android en Flutter para todas las operaciones del microservicio
`EjemploMicroServicioPersona`: listado paginado, búsqueda, detalle, creación,
edición y eliminación.

## Características

- Arquitectura separada por configuración, red, datos, dominio y presentación.
- Cliente HTTP centralizado con timeout, códigos de estado y mensajes de error.
- Paginación del servidor y búsqueda por nombre, identificación o tipo.
- Eliminación por deslizamiento con confirmación.
- Temas claro, oscuro y según el sistema, con preferencia persistente.
- Paleta institucional CUC: Pantone 288C (`#1E376C`) y 032C (`#EF4135`).
- Estados de carga, vacío, error, actualización por gesto y diseño adaptable.

## Ejecución local

1. Configura y ejecuta el microservicio con el perfil HTTPS:

   ```bash
   dotnet run --project ../EjemploMicroServicioPersona/EjemploMicroServicioPersona/EjemploMicroServicioPersona.csproj --launch-profile https
   ```

2. Instala las dependencias y ejecuta Flutter:

   ```bash
   flutter pub get
   flutter run
   ```

La URL base se lee desde un archivo `.env` en la raíz del proyecto. Usa
`.env.example` como plantilla y copia su contenido a `.env` antes de ejecutar
la app:

```env
API_BASE_URL=https://10.0.2.2:7231
```

Si no existe `.env` o la variable no está definida, la app usa estos valores
por defecto:

- `https://10.0.2.2:7231` en Android.
- `https://localhost:7231` en Web y en el resto de plataformas.

Para un dispositivo físico, cambia `API_BASE_URL` por la IP de tu equipo en la
red local o por la URL remota del servicio, siempre sin barra final. Mantén el
archivo `.env` fuera del control de versiones y reserva `.env.example` para la
plantilla compartida. Swagger se encuentra en
`https://localhost:7231/swagger/index.html`, pero el cliente consume la base
del servicio.

En modo debug, la app acepta certificados de desarrollo no confiables en
plataformas basadas en `dart:io` para facilitar la conexión al API. En
producción usa un certificado válido y confiable. Si trabajas con emulador o
dispositivo físico, sigue manteniendo la política CORS del microservicio
restringida a los orígenes desplegados.

## Calidad

```bash
dart format lib test
flutter analyze
flutter test
```
