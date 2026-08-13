package cr.ac.cuc.ejemplonativop5.data.remote

import cr.ac.cuc.ejemplonativop5.data.remote.dto.PersonaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Contrato Retrofit que representa los endpoints REST del microservicio de personas. */
interface PersonaApi {
    @GET("api/Persona")
    suspend fun getPeople(): Response<List<PersonaDto>>

    @GET("api/Persona/page")
    suspend fun getPeoplePage(@Query("pageNumber") pageNumber: Int, @Query("pageSize") pageSize: Int): Response<PagedPersonDto>

    @GET("api/Persona/{id}")
    suspend fun getPersonById(@Path("id") id: String): Response<PersonaDto>

    @POST("api/Persona")
    suspend fun createPerson(@Body person: PersonaDto): Response<PersonaDto>

    @PUT("api/Persona/{id}")
    suspend fun updatePerson(@Path("id") id: String, @Body person: PersonaDto): Response<PersonaDto>

    @DELETE("api/Persona/{id}")
    suspend fun deletePerson(@Path("id") id: String): Response<Unit>
}
