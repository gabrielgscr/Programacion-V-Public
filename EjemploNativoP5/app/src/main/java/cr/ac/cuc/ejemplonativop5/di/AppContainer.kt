package cr.ac.cuc.ejemplonativop5.di

import cr.ac.cuc.ejemplonativop5.BuildConfig
import cr.ac.cuc.ejemplonativop5.data.remote.PersonaApi
import cr.ac.cuc.ejemplonativop5.data.repository.PersonaRepositoryImpl
import cr.ac.cuc.ejemplonativop5.domain.repository.PersonaRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Contenedor manual de dependencias para mantener la creación de servicios fuera de la interfaz. */
class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.PERSONA_API_BASE_URL)
        .client(NetworkClientFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val personaApi: PersonaApi = retrofit.create(PersonaApi::class.java)
    val personaRepository: PersonaRepository = PersonaRepositoryImpl(personaApi)
}
