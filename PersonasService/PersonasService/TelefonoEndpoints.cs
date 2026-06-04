using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.OpenApi;
using PersonasService.DataAccess.Models;
namespace PersonasService;

public static class TelefonoEndpoints
{
    public static void MapTelefonoEndpoints (this IEndpointRouteBuilder routes)
    {
        var group = routes.MapGroup("/api/Telefono").WithTags(nameof(Telefono));

        group.MapGet("/", async (Ejemplo2Context db) =>
        {
            return await db.Telefono.ToListAsync();
        })
        .WithName("GetAllTelefonos")
        .WithOpenApi();

        group.MapGet("/{id}", async Task<Results<Ok<Telefono>, NotFound>> (int telefonoid, Ejemplo2Context db) =>
        {
            return await db.Telefono.AsNoTracking()
                .FirstOrDefaultAsync(model => model.TelefonoId == telefonoid)
                is Telefono model
                    ? TypedResults.Ok(model)
                    : TypedResults.NotFound();
        })
        .WithName("GetTelefonoById")
        .WithOpenApi();

        group.MapPut("/{id}", async Task<Results<Ok, NotFound>> (int telefonoid, Telefono telefono, Ejemplo2Context db) =>
        {
            var affected = await db.Telefono
                .Where(model => model.TelefonoId == telefonoid)
                .ExecuteUpdateAsync(setters => setters
                    .SetProperty(m => m.TelefonoId, telefono.TelefonoId)
                    .SetProperty(m => m.PersonaId, telefono.PersonaId)
                    .SetProperty(m => m.Telefono1, telefono.Telefono1)
                    );
            return affected == 1 ? TypedResults.Ok() : TypedResults.NotFound();
        })
        .WithName("UpdateTelefono")
        .WithOpenApi();

        group.MapPost("/", async (Telefono telefono, Ejemplo2Context db) =>
        {
            db.Telefono.Add(telefono);
            await db.SaveChangesAsync();
            return TypedResults.Created($"/api/Telefono/{telefono.TelefonoId}",telefono);
        })
        .WithName("CreateTelefono")
        .WithOpenApi();

        group.MapDelete("/{id}", async Task<Results<Ok, NotFound>> (int telefonoid, Ejemplo2Context db) =>
        {
            var affected = await db.Telefono
                .Where(model => model.TelefonoId == telefonoid)
                .ExecuteDeleteAsync();
            return affected == 1 ? TypedResults.Ok() : TypedResults.NotFound();
        })
        .WithName("DeleteTelefono")
        .WithOpenApi();
    }
}
