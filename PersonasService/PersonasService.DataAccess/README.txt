1. Instalar:
- Microsoft.EntityFrameworkCore.SqlServer
- Microsoft.EntityFrameworkCore.Tools

En ambos proyectos, agregar la referencia a la librería de datos en el program


2. Agregar la referencia del proyecto de datos al principal.

3. Seleccionar el proyecto de datos como proyecto predeterminado en la consola del administrador de paquetes.
Scaffold-DbContext Name=DefaultConnection Microsoft.EntityFrameworkCore.SqlServer -OutputDir Models -force -NoPluralize

4. Modificar el program para agregar el dbcontext

5. Crear el CRUD, pero antes modificar el EjemploContext para que use directamente la cadena de conexion

6. Modificar el client.cs para que permita nulos en el campo estado navigation





