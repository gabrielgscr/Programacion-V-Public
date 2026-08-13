package cr.ac.cuc.ejemplonativop5.ui.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cr.ac.cuc.ejemplonativop5.core.network.NetworkResult
import cr.ac.cuc.ejemplonativop5.domain.model.Persona
import cr.ac.cuc.ejemplonativop5.domain.repository.PersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** ViewModel que conserva el estado de la pantalla y coordina las operaciones del mantenimiento. */
class PersonasViewModel(private val repository: PersonaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PersonasUiState())
    val uiState = _uiState.asStateFlow()

    init { loadPeople() }

    fun loadPeople() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSearchResult = false) }
            when (val result = repository.getPeoplePage(_uiState.value.pageNumber, PAGE_SIZE)) {
                is NetworkResult.Success -> _uiState.update { it.copy(isLoading = false, people = result.data.items, pageNumber = result.data.pageNumber, totalPages = result.data.totalPages, totalCount = result.data.totalCount) }
                is NetworkResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun goToPage(page: Int) {
        if (page in 1.._uiState.value.totalPages && page != _uiState.value.pageNumber) {
            _uiState.update { it.copy(pageNumber = page) }
            loadPeople()
        }
    }

    fun searchById(id: String) {
        if (id.isBlank()) { loadPeople(); return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getPersonById(id.trim())) {
                is NetworkResult.Success -> _uiState.update { it.copy(isLoading = false, people = listOf(result.data), isSearchResult = true) }
                is NetworkResult.Error -> _uiState.update { it.copy(isLoading = false, people = emptyList(), isSearchResult = true, error = result.message) }
            }
        }
    }

    fun save(person: Persona, password: String?, isNew: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val result = if (isNew) repository.createPerson(person, password.orEmpty())
            else repository.updatePerson(person)
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
                loadPeople()
            } else {
                _uiState.update { it.copy(isSaving = false, error = (result as NetworkResult.Error).message) }
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            when (val result = repository.deletePerson(id)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    loadPeople()
                }
                is NetworkResult.Error -> _uiState.update { it.copy(isSaving = false, error = result.message) }
            }
        }
    }
}

/** Estado inmutable que la pantalla observa para representar carga, contenido y errores. */
data class PersonasUiState(
    val people: List<Persona> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val pageNumber: Int = 1,
    val totalPages: Int = 1,
    val totalCount: Int = 0,
    val isSearchResult: Boolean = false
)

private const val PAGE_SIZE = 10

/** Fábrica explícita que inyecta el repositorio sin acoplar el ViewModel a Android. */
class PersonasViewModelFactory(private val repository: PersonaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PersonasViewModel(repository) as T
}
