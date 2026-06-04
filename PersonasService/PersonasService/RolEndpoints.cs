using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.OpenApi;
using PersonasService.DataAccess.Models;
namespace PersonasService;

public static class RolEndpoints
{
    public static void MapRolEndpoints (this IEndpointRouteBuilder routes)
    {
        var group = routes.MapGroup("/api/Rol").WithTags(nameof(Rol));

        group.MapGet("/", async (Ejemplo2Context db) =>
        {
            return await db.Rol.ToListAsync();
        })
        .WithName("GetAllRols")
        .WithOpenApi();

        group.MapGet("/{id}", async Task<Results<Ok<Rol>, NotFound>> (int rolid, Ejemplo2Context db) =>
        {
            return await db.Rol.AsNoTracking()
                .FirstOrDefaultAsync(model => model.RolId == rolid)
                is Rol model
                    ? TypedResults.Ok(model)
                    : TypedResults.NotFound();
        })
        .WithName("GetRolById")
        .WithOpenApi();

        group.MapPut("/{id}", async Task<Results<Ok, NotFound>> (int rolid, Rol rol, Ejemplo2Context db) =>
        {
            var affected = await db.Rol
                .Where(model => model.RolId == rolid)
                .ExecuteUpdateAsync(setters => setters
                    .SetProperty(m => m.RolId, rol.RolId)
                    .SetProperty(m => m.Nombre, rol.Nombre)
                    );
            return affected == 1 ? TypedResults.Ok() : TypedResults.NotFound();
        })
        .WithName("UpdateRol")
        .WithOpenApi();

        group.MapPost("/", async (Rol rol, Ejemplo2Context db) =>
        {
            db.Rol.Add(rol);
            await db.SaveChangesAsync();
            return TypedResults.Created($"/api/Rol/{rol.RolId}",rol);
        })
        .WithName("CreateRol")
        .WithOpenApi();

        group.MapDelete("/{id}", async Task<Results<Ok, NotFound>> (int rolid, Ejemplo2Context db) =>
        {
            var affected = await db.Rol
                .Where(model => model.RolId == rolid)
                .ExecuteDeleteAsync();
            return affected == 1 ? TypedResults.Ok() : TypedResults.NotFound();
        })
        .WithName("DeleteRol")
        .WithOpenApi();
    }
}
