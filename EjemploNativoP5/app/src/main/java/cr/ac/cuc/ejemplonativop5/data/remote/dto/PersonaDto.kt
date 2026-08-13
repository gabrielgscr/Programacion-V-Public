package cr.ac.cuc.ejemplonativop5.data.remote.dto

import cr.ac.cuc.ejemplonativop5.domain.model.Persona

/** Modelo de transporte alineado con el JSON que recibe y devuelve el microservicio. */
data class PersonaDto(
    val personaId: String,
    val nombre: String,
    val tipo: Byte,
    val gender: String,
    val password: String? = null
)

/** Convierte el modelo de red en el modelo que utiliza el dominio y la interfaz. */
fun PersonaDto.toDomain() = Persona(personaId, nombre, tipo, gender)

/** Convierte el modelo de dominio al contrato esperado por el microservicio. */
fun Persona.toDto(password: String? = null) = PersonaDto(id, name, type, gender, password)
