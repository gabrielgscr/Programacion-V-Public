using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.OpenApi;
using PersonasService.DataAccess.Models;
namespace PersonasService;

public static class PersonaEndpoints
{
    public static void MapPersonaEndpoints (this IEndpointRouteBuilder routes)
    {
        var group = routes.MapGroup("/api/Persona").WithTags(nameof(Persona));

        group.MapGet("/", async (Ejemplo2Context db) =>
        {
            return await db.Persona.ToListAsync();
        })
        .WithName("GetAllPersonas")
        .WithOpenApi();

        group.MapGet("/{personaid}", async Task<Results<Ok<Persona>, NotFound>> (string personaid, Ejemplo2Context db) =>
        {
            return await db.Persona.AsNoTracking()
                .FirstOrDefaultAsync(model => model.PersonaId == personaid)
                is Persona model
                    ? TypedResults.Ok(model)
                    : TypedResults.NotFound();
        })
        .WithName("GetPersonaById")
        .WithOpenApi();

        group.MapPut("/{id}", async Task<Results<Ok, NotFound>> (string personaid, Persona persona, Ejemplo2Context db) =>
        {
            var affected = await db.Persona
                .Where(model => model.PersonaId == personaid)
                .ExecuteUpdateAsync(setters => setters
                    .SetProperty(m => m.PersonaId, persona.PersonaId)
                    .SetProperty(m => m.Nombre, persona.Nombre)
                    .SetProperty(m => m.Tipo, persona.Tipo)
                    .SetProperty(m => m.Gender, persona.Gender)
                    .SetProperty(m => m.Password, persona.Password)
                    );
            return affected == 1 ? TypedResults.Ok() : TypedResults.NotFound();
        })
        .WithName("UpdatePersona")
        .WithOpenApi();

        group.MapPost("/", async (Persona persona, Ejemplo2Context db) =>
        {
            db.Persona.Add(persona);
            await db.SaveChangesAsync();
            return TypedResults.Created($"/api/Persona/{persona.PersonaId}",persona);
        })
        .WithName("CreatePersona")
        .WithOpenApi();

        group.MapDelete("/{id}", async Task<Results<Ok, NotFound>> (string personaid, Ejemplo2Context db) =>
        {
            var affected = await db.Persona
                .Where(model => model.PersonaId == personaid)
                .ExecuteDeleteAsync();
            return affected == 1 ? TypedResults.Ok() : TypedResults.NotFound();
        })
        .WithName("DeletePersona")
        .WithOpenApi();
    }
}
