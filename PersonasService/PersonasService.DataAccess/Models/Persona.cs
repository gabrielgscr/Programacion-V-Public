using System;
using System.Collections.Generic;

namespace PersonasService.DataAccess.Models;

public partial class Persona
{
    public string PersonaId { get; set; } = null!;

    public string Nombre { get; set; } = null!;

    public byte Tipo { get; set; }

    public string Gender { get; set; } = null!;

    public string Password { get; set; } = null!;

    public virtual ICollection<Telefono> Telefono { get; set; } = new List<Telefono>();

    public virtual ICollection<Rol> Rol { get; set; } = new List<Rol>();
}
