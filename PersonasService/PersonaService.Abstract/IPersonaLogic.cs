using PersonasService.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PersonaService.Abstract
{
    public interface IPersonaLogic
    {

        Task<BusinessLogicResponse> CreatePersona(PersonaDAO persona);

    }
}
