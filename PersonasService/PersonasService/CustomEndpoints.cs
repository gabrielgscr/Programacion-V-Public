using Microsoft.AspNetCore.Mvc;
using MiniValidation;
using PersonaService.Abstract;
using PersonasService.DataAccess.Models;
using PersonasService.Models;

namespace PersonasService
{
    public static class CustomEndpoints
    {
        public static void MapCustomEndpoints(this IEndpointRouteBuilder routes)
        {
            var group = routes.MapGroup("/api/PersonaService").WithTags("Endpoints personalizados");
            group.MapPost("/", async ([FromBody]PersonaDAO persona, [FromHeader]string secretKey, IPersonaLogic logic, Ejemplo2Context db) =>
            {

                persona.Password = secretKey;
                if (!MiniValidator.TryValidate(persona, out var errors))
                {
                    return Results.BadRequest(new { codigo = -2, mensaje = "Datos incorrectos", errores = errors });
                }
               
                var response = await logic.CreatePersona(persona);
                return response.StatusCode switch
                {
                    201 => Results.Created($"/api/Persona/{persona.PersonaId}", new { codigo = 0, mensaje = "Datos creados", datos = response.ResponseObject }),
                    404 => Results.NotFound(response),
                    _ => Results.Json(new { codigo = -1, mensaje = response.Message }, 
                                        statusCode: StatusCodes.Status500InternalServerError)
                };
            })
            .WithName("CreatePersonaCustom")
            .WithOpenApi();
        }

    }
}
