using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace PersonasService.DataAccess.Models;

public partial class Telefono
{
    public int TelefonoId { get; set; }

    public string PersonaId { get; set; } = null!;

    public string Telefono1 { get; set; } = null!;

    [JsonIgnore]
    public virtual Persona Persona { get; set; } = null!;
}
