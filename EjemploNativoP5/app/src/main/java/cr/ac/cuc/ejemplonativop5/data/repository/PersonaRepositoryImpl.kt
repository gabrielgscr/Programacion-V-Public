package cr.ac.cuc.ejemplonativop5.data.repository

import cr.ac.cuc.ejemplonativop5.core.network.NetworkResult
import cr.ac.cuc.ejemplonativop5.data.remote.PersonaApi
import cr.ac.cuc.ejemplonativop5.data.remote.dto.toDomain
import cr.ac.cuc.ejemplonativop5.data.remote.dto.toDto
import cr.ac.cuc.ejemplonativop5.domain.model.Persona
import cr.ac.cuc.ejemplonativop5.domain.repository.PersonaRepository
import cr.ac.cuc.ejemplonativop5.domain.repository.PagedPeople
import java.io.IOException

/** Implementación del repositorio que encapsula Retrofit, mapeos y errores de red. */
class PersonaRepositoryImpl(private val api: PersonaApi) : PersonaRepository {
    override suspend fun getPeople(): NetworkResult<List<Persona>> = try {
        val response = api.getPeople()
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()?.map { it.toDomain() }.orEmpty())
        } else {
            NetworkResult.Error(response.errorBody()?.string(), response.code())
        }
    } catch (_: IOException) {
        NetworkResult.Error()
    } catch (_: Exception) {
        NetworkResult.Error()
    }

    override suspend fun createPerson(person: Persona, password: String): NetworkResult<Unit> = requestUnit {
        api.createPerson(person.toDto(password))
    }

    override suspend fun getPeoplePage(pageNumber: Int, pageSize: Int): NetworkResult<PagedPeople> = try {
        val response = api.getPeoplePage(pageNumber, pageSize)
        val data = response.body()
        if (response.isSuccessful && data != null) NetworkResult.Success(PagedPeople(data.items.map { it.toDomain() }, data.pageNumber, data.totalPages, data.totalCount))
        else NetworkResult.Error(response.errorBody()?.string(), response.code())
    } catch (_: Exception) { NetworkResult.Error() }

    override suspend fun getPersonById(id: String): NetworkResult<Persona> = try {
        val response = api.getPersonById(id)
        val data = response.body()
        if (response.isSuccessful && data != null) NetworkResult.Success(data.toDomain())
        else NetworkResult.Error(response.errorBody()?.string(), response.code())
    } catch (_: Exception) { NetworkResult.Error() }

    override suspend fun updatePerson(person: Persona): NetworkResult<Unit> = requestUnit {
        api.updatePerson(person.id, person.toDto())
    }

    override suspend fun deletePerson(id: String): NetworkResult<Unit> = requestUnit {
        api.deletePerson(id)
    }

    private suspend fun requestUnit(call: suspend () -> retrofit2.Response<*>): NetworkResult<Unit> = try {
        val response = call()
        if (response.isSuccessful) NetworkResult.Success(Unit)
        else NetworkResult.Error(response.errorBody()?.string(), response.code())
    } catch (_: IOException) {
        NetworkResult.Error()
    } catch (_: Exception) {
        NetworkResult.Error()
    }
}
