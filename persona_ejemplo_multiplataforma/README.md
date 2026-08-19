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

1. Configura y ejecuta el microservicio con el perfil HTTP:

   ```bash
   dotnet run --project ../EjemploMicroServicioPersona/EjemploMicroServicioPersona/EjemploMicroServicioPersona.csproj --launch-profile http
   ```

2. Instala las dependencias y ejecuta Flutter:

   ```bash
   flutter pub get
   flutter run
   ```

El valor predeterminado es `http://10.0.2.2:5200` en el emulador Android y
`http://localhost:5200` en otros destinos. Para un dispositivo físico o un
ambiente remoto, suministra la URL sin barra final:

```bash
flutter run --dart-define=API_BASE_URL=http://192.168.1.20:5200
```

En producción utiliza HTTPS y restringe la política CORS del microservicio a
los orígenes desplegados.

## Calidad

```bash
dart format lib test
flutter analyze
flutter test
```
