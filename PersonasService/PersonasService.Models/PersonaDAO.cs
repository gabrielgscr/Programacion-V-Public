using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace PersonasService.Models
{
    public class PersonaDAO
    {
        [Required(AllowEmptyStrings = false, ErrorMessage = "El ID de la persona es requerido")]
        public string PersonaId { get; set; } = null!;

        [Required(AllowEmptyStrings = false, ErrorMessage = "El nombre es requerido")]
        [MinLength(5, ErrorMessage = "El nombre no puede tener menos de 5 caracteres")]
        public string Nombre { get; set; } = null!;

        [Required(ErrorMessage = "El tipo de persona es requerido")]
        [Range(1, 3, ErrorMessage = "El tipo de persona debe ser 1, 2 o 3")]
        public byte Tipo { get; set; }

        public string? Telefono { get; set; } = null!;

        [Required(AllowEmptyStrings = false, ErrorMessage = "El rol de la persona es requerido")]
        public string Rol { get; set; } = null!;

        public string? Gender { get; set; } = null!;

        [JsonIgnore]
        [Required(AllowEmptyStrings = false, ErrorMessage = "La contraseña es requerida")]
        [MaxLength(20, ErrorMessage = "La contraseña no puede tener más de 20 caracteres")]
        [MinLength(8, ErrorMessage = "La contraseña no puede tener menos de 8 caracteres")]
        public string Password { get; set; } = string.Empty;
    }
}
