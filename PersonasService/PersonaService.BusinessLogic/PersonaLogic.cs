using Microsoft.Extensions.Configuration;
using PersonaService.Abstract;
using PersonasService.DataAccess.Models;
using PersonasService.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PersonaService.BusinessLogic
{
    public class PersonaLogic : IPersonaLogic
    {

        private Ejemplo2Context context;
        private IConfiguration _config;

        public PersonaLogic(Ejemplo2Context context, IConfiguration config) 
        { 
            this.context = context;
            this._config = config;
        }

        public async Task<BusinessLogicResponse> CreatePersona(PersonaDAO persona)
        {
            try 
            { 
                // Verifica si existe una persona con el mismo id de previo
                if (context.Persona.Any(p => p.PersonaId == persona.PersonaId))
                {
                    return new BusinessLogicResponse
                    {
                        StatusCode = 409,
                        Message = "La persona ya existe"
                    };
                }

                //Asumimos que los datos vienen validados
                var personaEntity = new Persona
                {
                    PersonaId = persona.PersonaId,
                    Nombre = persona.Nombre,
                    Tipo = persona.Tipo
                };
                //le agregamos el telefono
                personaEntity.Telefono.Add(new Telefono
                {
                    Telefono1 = persona.Telefono!
                });
                //Buscamos el rol por su nombre
                var rol = context.Rol.FirstOrDefault(r => r.Nombre == persona.Rol);
                //Si existe el rol lo agregamos a la persona
                if (rol != null)
                {
                    //Se obtiene el genero
                    ServiceConsumer service = new ServiceConsumer(_config);
                    string firstName = persona.Nombre.Split(" ")[0];
                    GenderInformation information = await service.GetGender(firstName);
                    personaEntity.Gender = information.Gender;
                    //Se agregan los roles
                    personaEntity.Rol.Add(rol);
                    context.Persona.Add(personaEntity);
                    context.SaveChanges();
                    return new BusinessLogicResponse
                    {
                        StatusCode = 201,
                        Message = "Persona creada",
                        ResponseObject = personaEntity
                    };
                }
                else
                {
                    return new BusinessLogicResponse
                    {
                        StatusCode = 404,
                        Message = "El rol no existe"
                    };
                }
            }
            catch (Exception ex)
            {
                return new BusinessLogicResponse
                {
                    StatusCode = 500,
                    Message = ex.Message
                };
            }
        }
    }
}
