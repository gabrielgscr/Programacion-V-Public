using Microsoft.Extensions.Configuration;
using PersonasService.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace PersonaService.BusinessLogic
{
    // Esta clase va a obtener de configuracion una URL e invocara un servicio rest externo
    public class ServiceConsumer
    {

        private IConfiguration _config;
  
        public ServiceConsumer(IConfiguration config)
        { 
            this._config = config;
        }

        public async Task<GenderInformation> GetGender(string name)
        {
            //Obtenemos la URL del servicio
            var url = _config["GenderizeApi"];
            //Aqui iria el codigo para invocar el servicio
            using HttpClient cliente = new HttpClient();
            var respuesta = await cliente.GetAsync($"{url}?name={name}");
            respuesta.EnsureSuccessStatusCode();
            GenderInformation gender = GenderInformation.FromJson(await respuesta.Content.ReadAsStringAsync());
            return gender;

        }

    }
}
