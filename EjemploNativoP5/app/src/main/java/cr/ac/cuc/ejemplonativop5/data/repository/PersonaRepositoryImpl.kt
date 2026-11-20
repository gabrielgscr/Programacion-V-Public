package cr.ac.cuc.ejemplonativop5.data.repository

import android.util.Log
import cr.ac.cuc.ejemplonativop5.BuildConfig
import cr.ac.cuc.ejemplonativop5.core.network.NetworkResult
import cr.ac.cuc.ejemplonativop5.data.remote.PersonaApi
import cr.ac.cuc.ejemplonativop5.data.remote.dto.toDomain
import cr.ac.cuc.ejemplonativop5.data.remote.dto.toDto
import cr.ac.cuc.ejemplonativop5.domain.model.Persona
import cr.ac.cuc.ejemplonativop5.domain.repository.PersonaRepository
import cr.ac.cuc.ejemplonativop5.domain.repository.PagedPeople
import kotlinx.coroutines.CancellationException
import retrofit2.Response

/** Implementación del repositorio que encapsula Retrofit, mapeos y errores de red. */
class PersonaRepositoryImpl(private val api: PersonaApi) : PersonaRepository {
    override suspend fun getPeople(): NetworkResult<List<Persona>> = try {
        val response = api.getPeople()
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()?.map { it.toDomain() }.orEmpty())
        } else {
            responseError(response)
        }
    } catch (exception: Exception) {
        requestError("getPeople", exception)
    }

    override suspend fun createPerson(person: Persona, password: String): NetworkResult<Unit> = requestUnit("createPerson") {
        api.createPerson(person.toDto(password))
    }

    override suspend fun getPeoplePage(pageNumber: Int, pageSize: Int): NetworkResult<PagedPeople> = try {
        val response = api.getPeoplePage(pageNumber, pageSize)
        val data = response.body()
        if (response.isSuccessful && data != null) NetworkResult.Success(PagedPeople(data.items.map { it.toDomain() }, data.pageNumber, data.totalPages, data.totalCount))
        else responseError(response)
    } catch (exception: Exception) { requestError("getPeoplePage", exception) }

    override suspend fun getPersonById(id: String): NetworkResult<Persona> = try {
        val response = api.getPersonById(id)
        val data = response.body()
        if (response.isSuccessful && data != null) NetworkResult.Success(data.toDomain())
        else responseError(response)
    } catch (exception: Exception) { requestError("getPersonById", exception) }

    override suspend fun updatePerson(person: Persona): NetworkResult<Unit> = requestUnit("updatePerson") {
        api.updatePerson(person.id, person.toDto())
    }

    override suspend fun deletePerson(id: String): NetworkResult<Unit> = requestUnit("deletePerson") {
        api.deletePerson(id)
    }

    private suspend fun requestUnit(operation: String, call: suspend () -> Response<*>): NetworkResult<Unit> = try {
        val response = call()
        if (response.isSuccessful) NetworkResult.Success(Unit)
        else responseError(response)
    } catch (exception: Exception) {
        requestError(operation, exception)
    }

    /** Registra el estado HTTP sin incluir cuerpos que puedan contener datos personales. */
    private fun responseError(response: Response<*>): NetworkResult.Error {
        if (BuildConfig.DEBUG) {
            val request = response.raw().request
            val reason = if (response.isSuccessful) "Respuesta sin cuerpo" else "Error HTTP"
            Log.e(TAG, "$reason: ${request.method} ${request.url.host}, HTTP ${response.code()} ${response.message()}")
        }
        return NetworkResult.Error(response.errorBody()?.string(), response.code())
    }

    /** Conserva la excepción completa para distinguir errores TLS, DNS, conexión y conversión. */
    private fun requestError(operation: String, exception: Exception): NetworkResult.Error {
        if (exception is CancellationException) throw exception
        if (BuildConfig.DEBUG) Log.e(TAG, "Falló $operation; API=${BuildConfig.PERSONA_API_BASE_URL}", exception)
        return NetworkResult.Error()
    }

    private companion object {
        const val TAG = "PersonaRepository"
    }
}
