using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace PersonasService.DataAccess.Models;

public partial class Rol
{
    public int RolId { get; set; }

    public string Nombre { get; set; } = null!;

    [JsonIgnore]
    public virtual ICollection<Persona> Persona { get; set; } = new List<Persona>();
}
