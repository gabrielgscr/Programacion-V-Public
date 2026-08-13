package cr.ac.cuc.ejemplonativop5.ui.people

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cr.ac.cuc.ejemplonativop5.R
import cr.ac.cuc.ejemplonativop5.domain.model.Persona
import cr.ac.cuc.ejemplonativop5.ui.components.AppTextField
import cr.ac.cuc.ejemplonativop5.ui.components.PrimaryButton
import cr.ac.cuc.ejemplonativop5.settings.AppSettings
import cr.ac.cuc.ejemplonativop5.settings.ThemeMode

/** Ruta de Compose que conecta el ViewModel con el mantenimiento de personas. */
@Composable
fun PersonasRoute(viewModel: PersonasViewModel, settings: AppSettings, onThemeChanged: (ThemeMode) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PersonasScreen(
        state = state,
        onRefresh = viewModel::loadPeople,
        onPage = viewModel::goToPage,
        onSearch = viewModel::searchById,
        onSave = viewModel::save,
        onDelete = viewModel::delete,
        settings = settings,
        onThemeChanged = onThemeChanged
    )
}

/** Pantalla principal con listado, formulario reutilizado para crear/editar y eliminación confirmada. */
@Composable
private fun PersonasScreen(
    state: PersonasUiState,
    onRefresh: () -> Unit,
    onPage: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onSave: (Persona, String?, Boolean, () -> Unit) -> Unit,
    onDelete: (String) -> Unit,
    settings: AppSettings,
    onThemeChanged: (ThemeMode) -> Unit
) {
    var editingPerson by remember { mutableStateOf<Persona?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<Persona?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    when {
        isCreating || editingPerson != null -> PersonFormScreen(
            person = editingPerson,
            isSaving = state.isSaving,
            error = state.error,
            onBack = { isCreating = false; editingPerson = null },
            onSave = { person, password, isNew ->
                onSave(person, password, isNew) { isCreating = false; editingPerson = null }
            }
        )
        else -> PeopleListScreen(
            state = state,
            onRefresh = onRefresh,
            onPage = onPage,
            onSearch = onSearch,
            onCreate = { isCreating = true },
            onEdit = { editingPerson = it },
            onDelete = { deleteCandidate = it },
            onSettings = { showSettings = true }
        )
    }

    if (showSettings) SettingsDialog(settings, { showSettings = false }, onThemeChanged)

    deleteCandidate?.let { person ->
        DeletePersonDialog(
            person = person,
            onDismiss = { deleteCandidate = null },
            onConfirm = { onDelete(person.id); deleteCandidate = null }
        )
    }
}

/** Lista las personas recibidas del API y ofrece las acciones de mantenimiento. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeopleListScreen(
    state: PersonasUiState,
    onRefresh: () -> Unit,
    onPage: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onCreate: () -> Unit,
    onEdit: (Persona) -> Unit,
    onDelete: (Persona) -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.people)) }, actions = { TextButton(onClick = onSettings) { Text(stringResource(R.string.settings)) } }) },
        floatingActionButton = { FloatingActionButton(onClick = onCreate) { Text("+") } }
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            state.error != null -> ErrorContent(state.error, onRefresh, Modifier.padding(padding))
            state.people.isEmpty() -> EmptyContent(Modifier.padding(padding))
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                item { PersonSearch(onSearch) }
                items(state.people, key = { it.id }) { person ->
                    PersonCard(person, onEdit, onDelete)
                }
                if (!state.isSearchResult) item { PaginationControls(state.pageNumber, state.totalPages, state.totalCount, onPage) }
            }
        }
    }
}

/** Diálogo que permite cambiar idioma y tema sin mezclar preferencias con las pantallas de negocio. */
@Composable
private fun SettingsDialog(settings: AppSettings, onDismiss: () -> Unit, onThemeChanged: (ThemeMode) -> Unit) {
    val activity = LocalContext.current as Activity
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.settings)) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.language))
            Row { TextButton(onClick = { settings.setLanguage("es", activity) }) { Text(stringResource(R.string.spanish)) }; TextButton(onClick = { settings.setLanguage("en", activity) }) { Text(stringResource(R.string.english)) } }
            Text(stringResource(R.string.theme))
            Row { TextButton(onClick = { settings.themeMode = ThemeMode.SYSTEM; onThemeChanged(ThemeMode.SYSTEM) }) { Text(stringResource(R.string.system_theme)) }; TextButton(onClick = { settings.themeMode = ThemeMode.LIGHT; onThemeChanged(ThemeMode.LIGHT) }) { Text(stringResource(R.string.light_theme)) }; TextButton(onClick = { settings.themeMode = ThemeMode.DARK; onThemeChanged(ThemeMode.DARK) }) { Text(stringResource(R.string.dark_theme)) } }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } })

/** Campo de búsqueda que consulta directamente una persona por su identificación primaria. */
}

@Composable
private fun PersonSearch(onSearch: (String) -> Unit) {
    var id by remember { mutableStateOf("") }
    AppTextField(id, { id = it }, stringResource(R.string.search_id))
    Spacer(Modifier.height(4.dp))
    PrimaryButton(stringResource(R.string.search), onClick = { onSearch(id) })
}

/** Controles reutilizables para desplazarse por las páginas proporcionadas por el microservicio. */
@Composable
private fun PaginationControls(page: Int, totalPages: Int, totalCount: Int, onPage: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(stringResource(R.string.page_status, page, totalPages, totalCount))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(enabled = page > 1, onClick = { onPage(page - 1) }) { Text(stringResource(R.string.previous)) }
            TextButton(enabled = page < totalPages, onClick = { onPage(page + 1) }) { Text(stringResource(R.string.next)) }
        }
    }
}

/** Tarjeta reutilizable que presenta el resumen y acciones de una persona. */
@Composable
private fun PersonCard(person: Persona, onEdit: (Persona) -> Unit, onDelete: (Persona) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(person.name, style = MaterialTheme.typography.titleMedium)
            Text(person.id, style = MaterialTheme.typography.bodyMedium)
            Text("${stringResource(R.string.person_type)}: ${person.type} · ${person.gender}")
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { onEdit(person) }) { Text(stringResource(R.string.edit)) }
                TextButton(onClick = { onDelete(person) }) { Text(stringResource(R.string.delete)) }
            }
        }
    }
}

/** Formulario común que valida y envía una creación o edición al ViewModel. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonFormScreen(
    person: Persona?,
    isSaving: Boolean,
    error: String?,
    onBack: () -> Unit,
    onSave: (Persona, String?, Boolean) -> Unit
) {
    var id by remember(person) { mutableStateOf(person?.id.orEmpty()) }
    var name by remember(person) { mutableStateOf(person?.name.orEmpty()) }
    var type by remember(person) { mutableStateOf(person?.type?.toString().orEmpty()) }
    var gender by remember(person) { mutableStateOf(person?.gender.orEmpty()) }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val isNew = person == null
    val valid = id.isNotBlank() && name.length >= 5 && type.toIntOrNull() in 1..3 &&
        gender.isNotBlank() && gender.length <= 10 && (!isNew || password.length >= 8)

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(if (isNew) R.string.create_person else R.string.edit_person)) })
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            item { Text(stringResource(R.string.person_form), style = MaterialTheme.typography.titleMedium) }
            item { AppTextField(id, { id = it }, stringResource(R.string.person_id), submitted && id.isBlank(), if (submitted && id.isBlank()) stringResource(R.string.required_field) else null, readOnly = !isNew) }
            item { AppTextField(name, { name = it }, stringResource(R.string.name), submitted && name.length < 5, if (submitted && name.length < 5) stringResource(R.string.name_min_length) else null) }
            item { AppTextField(type, { type = it.filter(Char::isDigit) }, stringResource(R.string.person_type), submitted && type.toIntOrNull() !in 1..3, if (submitted && type.toIntOrNull() !in 1..3) stringResource(R.string.type_range) else null) }
            item { AppTextField(gender, { gender = it }, stringResource(R.string.gender), submitted && (gender.isBlank() || gender.length > 10), if (submitted && gender.isBlank()) stringResource(R.string.required_field) else if (submitted && gender.length > 10) stringResource(R.string.gender_max_length) else null) }
            if (isNew) item { AppTextField(password, { password = it }, stringResource(R.string.password), submitted && password.length < 8, if (submitted && password.length < 8) stringResource(R.string.password_min_length) else stringResource(R.string.password_hint), visualTransformation = PasswordVisualTransformation()) }
            if (error != null) item { Text(error.ifBlank { stringResource(R.string.network_error) }, color = MaterialTheme.colorScheme.error) }
            item {
                PrimaryButton(stringResource(if (isNew) R.string.create_person else R.string.save_changes), !isSaving) {
                    submitted = true
                    if (valid) onSave(Persona(id.trim(), name.trim(), type.toByte(), gender.trim()), password.takeIf { isNew }, isNew)
                }
            }
            item { TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.cancel)) } }
        }
    }
}

/** Diálogo de confirmación que evita eliminaciones accidentales. */
@Composable
private fun DeletePersonDialog(person: Persona, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_title)) },
        text = { Text("${person.name}. ${stringResource(R.string.delete_message)}") },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

/** Estado visual reutilizable mientras se espera una respuesta del microservicio. */
@Composable
private fun LoadingContent(modifier: Modifier) = Column(
    modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
) { CircularProgressIndicator(); Spacer(Modifier.height(12.dp)); Text(stringResource(R.string.loading)) }

/** Estado visual reutilizable cuando no existen registros. */
@Composable
private fun EmptyContent(modifier: Modifier) = Column(
    modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
) { Text(stringResource(R.string.empty_people)) }

/** Estado visual reutilizable que permite recuperarse de errores de red o del servicio. */
@Composable
private fun ErrorContent(error: String, onRetry: () -> Unit, modifier: Modifier) = Column(
    modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
) { Text(error.ifBlank { stringResource(R.string.network_error) }); Spacer(Modifier.height(12.dp)); PrimaryButton(stringResource(R.string.retry), onClick = onRetry) }
