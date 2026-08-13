package cr.ac.cuc.ejemplonativop5.domain.model

/** Entidad de negocio independiente de la tecnología usada para consumir o mostrar los datos. */
data class Persona(
    val id: String,
    val name: String,
    val type: Byte,
    val gender: String
)
