package cr.ac.cuc.ejemplonativop5.domain.repository

import cr.ac.cuc.ejemplonativop5.core.network.NetworkResult
import cr.ac.cuc.ejemplonativop5.domain.model.Persona

/** Abstracción de las operaciones de mantenimiento de personas disponibles para la capa de presentación. */
interface PersonaRepository {
    suspend fun getPeople(): NetworkResult<List<Persona>>
    suspend fun getPeoplePage(pageNumber: Int, pageSize: Int): NetworkResult<PagedPeople>
    suspend fun getPersonById(id: String): NetworkResult<Persona>
    suspend fun createPerson(person: Persona, password: String): NetworkResult<Unit>
    suspend fun updatePerson(person: Persona): NetworkResult<Unit>
    suspend fun deletePerson(id: String): NetworkResult<Unit>
}

/** Resultado de dominio para conservar los datos de navegación de la respuesta paginada. */
data class PagedPeople(val items: List<Persona>, val pageNumber: Int, val totalPages: Int, val totalCount: Int)
