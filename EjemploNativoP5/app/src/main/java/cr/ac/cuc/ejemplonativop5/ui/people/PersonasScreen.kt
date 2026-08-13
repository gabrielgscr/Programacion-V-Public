package cr.ac.cuc.ejemplonativop5.ui.people

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cr.ac.cuc.ejemplonativop5.R
import cr.ac.cuc.ejemplonativop5.domain.model.Persona
import cr.ac.cuc.ejemplonativop5.settings.AppSettings
import cr.ac.cuc.ejemplonativop5.settings.ThemeMode
import cr.ac.cuc.ejemplonativop5.ui.components.AppTextField
import cr.ac.cuc.ejemplonativop5.ui.components.PrimaryButton

/** Ruta Compose que conecta el estado del mantenimiento con sus pantallas y preferencias. */
@Composable fun PersonasRoute(viewModel: PersonasViewModel, settings: AppSettings, onThemeChanged: (ThemeMode) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PersonasScreen(state, viewModel::loadPeople, viewModel::goToPage, viewModel::searchById, viewModel::save, viewModel::delete, settings, onThemeChanged)
}

/** Coordina listado, formulario, confirmación de borrado y configuración visual. */
@Composable private fun PersonasScreen(state: PersonasUiState, onRefresh: () -> Unit, onPage: (Int) -> Unit, onSearch: (String) -> Unit, onSave: (Persona, String?, Boolean, () -> Unit) -> Unit, onDelete: (String) -> Unit, settings: AppSettings, onThemeChanged: (ThemeMode) -> Unit) {
    var editing by remember { mutableStateOf<Persona?>(null) }; var creating by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<Persona?>(null) }; var configuring by remember { mutableStateOf(false) }
    when { creating || editing != null -> PersonFormScreen(editing, state.isSaving, state.error, { creating = false; editing = null }) { person, password, isNew -> onSave(person, password, isNew) { creating = false; editing = null } }
        else -> PeopleListScreen(state, onRefresh, onPage, onSearch, { creating = true }, { editing = it }, { deleting = it }, { configuring = true }) }
    deleting?.let { DeletePersonDialog(it, { deleting = null }) { onDelete(it.id); deleting = null } }
    if (configuring) SettingsDialog(settings, { configuring = false }, onThemeChanged)
}

/** Presenta personas con acciones accesibles, búsqueda e indicadores de paginación. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun PeopleListScreen(state: PersonasUiState, onRefresh: () -> Unit, onPage: (Int) -> Unit, onSearch: (String) -> Unit, onCreate: () -> Unit, onEdit: (Persona) -> Unit, onDelete: (Persona) -> Unit, onSettings: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.people)) }, actions = { IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, stringResource(R.string.settings)) } }) }, floatingActionButton = { FloatingActionButton(onClick = onCreate) { Icon(Icons.Default.Add, stringResource(R.string.add_person)) } }) { padding ->
        when { state.isLoading -> LoadingContent(Modifier.padding(padding)); state.error != null -> ErrorContent(state.error, onRefresh, Modifier.padding(padding)); else -> LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { PersonSearch(onSearch) }
            if (state.people.isEmpty()) item { EmptyContent(Modifier.fillParentMaxSize()) }
            items(state.people, key = { it.id }) { PersonCard(it, onEdit, onDelete) }
            if (!state.isSearchResult && state.people.isNotEmpty()) item { PaginationControls(state.pageNumber, state.totalPages, state.totalCount, onPage) }
        } }
    }
}

/** Permite buscar por llave primaria y limpiar la búsqueda para volver al listado paginado. */
@Composable private fun PersonSearch(onSearch: (String) -> Unit) { var id by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) { OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text(stringResource(R.string.search_id)) }, leadingIcon = { Icon(Icons.Default.Search, null) }, trailingIcon = { if (id.isNotBlank()) IconButton(onClick = { id = ""; onSearch("") }) { Icon(Icons.Default.Close, stringResource(R.string.clear_search)) } }, singleLine = true, modifier = Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); FilledIconButton(onClick = { onSearch(id) }) { Icon(Icons.Default.Search, stringResource(R.string.search)) } }
}

/** Tarjeta compacta con identidad, resumen y acciones mediante iconos accesibles. */
@Composable private fun PersonCard(person: Persona, onEdit: (Persona) -> Unit, onDelete: (Persona) -> Unit) { Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(person.name, style = MaterialTheme.typography.titleMedium); Text(person.id, style = MaterialTheme.typography.bodyMedium); Text("${stringResource(R.string.person_type)} ${person.type} · ${person.gender}", style = MaterialTheme.typography.bodySmall) }; IconButton(onClick = { onEdit(person) }) { Icon(Icons.Default.Edit, stringResource(R.string.edit_person_label, person.name)) }; IconButton(onClick = { onDelete(person) }) { Icon(Icons.Default.Delete, stringResource(R.string.delete_person, person.name), tint = MaterialTheme.colorScheme.error) } } } }

/** Permite avanzar o retroceder en el resultado paginado del microservicio. */
@Composable private fun PaginationControls(page: Int, totalPages: Int, totalCount: Int, onPage: (Int) -> Unit) = Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.page_status, page, totalPages, totalCount)); Row { IconButton(enabled = page > 1, onClick = { onPage(page - 1) }) { Icon(Icons.Default.ChevronLeft, stringResource(R.string.previous)) }; IconButton(enabled = page < totalPages, onClick = { onPage(page + 1) }) { Icon(Icons.Default.ChevronRight, stringResource(R.string.next)) } } }

/** Muestra y persiste las preferencias de idioma y esquema de color. */
@Composable private fun SettingsDialog(settings: AppSettings, onDismiss: () -> Unit, onThemeChanged: (ThemeMode) -> Unit) { val activity = LocalContext.current as Activity; AlertDialog(onDismissRequest = onDismiss, icon = { Icon(Icons.Default.Settings, null) }, title = { Text(stringResource(R.string.settings)) }, text = { Column { Text(stringResource(R.string.language)); Row { TextButton(onClick = { settings.setLanguage("es", activity) }) { Icon(Icons.Default.Language, null); Text(stringResource(R.string.spanish)) }; TextButton(onClick = { settings.setLanguage("en", activity) }) { Text(stringResource(R.string.english)) } }; Text(stringResource(R.string.theme)); Row { IconButton(onClick = { settings.themeMode = ThemeMode.SYSTEM; onThemeChanged(ThemeMode.SYSTEM) }) { Icon(Icons.Default.BrightnessAuto, stringResource(R.string.system_theme)) }; IconButton(onClick = { settings.themeMode = ThemeMode.LIGHT; onThemeChanged(ThemeMode.LIGHT) }) { Icon(Icons.Default.LightMode, stringResource(R.string.light_theme)) }; IconButton(onClick = { settings.themeMode = ThemeMode.DARK; onThemeChanged(ThemeMode.DARK) }) { Icon(Icons.Default.DarkMode, stringResource(R.string.dark_theme)) } } } }, confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } }) }

/** Formulario común de creación y edición con navegación y validación local. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun PersonFormScreen(person: Persona?, isSaving: Boolean, error: String?, onBack: () -> Unit, onSave: (Persona, String?, Boolean) -> Unit) { var id by remember(person) { mutableStateOf(person?.id.orEmpty()) }; var name by remember(person) { mutableStateOf(person?.name.orEmpty()) }; var type by remember(person) { mutableStateOf(person?.type?.toString().orEmpty()) }; var gender by remember(person) { mutableStateOf(person?.gender.orEmpty()) }; var password by remember { mutableStateOf("") }; var submitted by remember { mutableStateOf(false) }; val isNew = person == null; val valid = id.isNotBlank() && name.length >= 5 && type.toIntOrNull() in 1..3 && gender.isNotBlank() && gender.length <= 10 && (!isNew || password.length >= 8)
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(if (isNew) R.string.create_person else R.string.edit_person)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } }) }) { padding -> LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { AppTextField(id, { id = it }, stringResource(R.string.person_id), submitted && id.isBlank(), if (submitted && id.isBlank()) stringResource(R.string.required_field) else null, readOnly = !isNew) }; item { AppTextField(name, { name = it }, stringResource(R.string.name), submitted && name.length < 5, if (submitted && name.length < 5) stringResource(R.string.name_min_length) else null) }; item { AppTextField(type, { type = it.filter(Char::isDigit) }, stringResource(R.string.person_type), submitted && type.toIntOrNull() !in 1..3, if (submitted && type.toIntOrNull() !in 1..3) stringResource(R.string.type_range) else null) }; item { AppTextField(gender, { gender = it }, stringResource(R.string.gender), submitted && (gender.isBlank() || gender.length > 10), null) }; if (isNew) item { AppTextField(password, { password = it }, stringResource(R.string.password), submitted && password.length < 8, stringResource(R.string.password_hint), visualTransformation = PasswordVisualTransformation()) }; if (error != null) item { Text(error, color = MaterialTheme.colorScheme.error) }; item { PrimaryButton(stringResource(if (isNew) R.string.create_person else R.string.save_changes), !isSaving) { submitted = true; if (valid) onSave(Persona(id.trim(), name.trim(), type.toByte(), gender.trim()), password.takeIf { isNew }, isNew) } } } } }

/** Confirma una acción destructiva antes de solicitar la eliminación al API. */
@Composable private fun DeletePersonDialog(person: Persona, onDismiss: () -> Unit, onConfirm: () -> Unit) = AlertDialog(onDismissRequest = onDismiss, icon = { Icon(Icons.Default.WarningAmber, null, tint = MaterialTheme.colorScheme.error) }, title = { Text(stringResource(R.string.delete_title)) }, text = { Text("${person.name}. ${stringResource(R.string.delete_message)}") }, confirmButton = { TextButton(onClick = onConfirm) { Icon(Icons.Default.Delete, null); Text(stringResource(R.string.delete)) } }, dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } })

/** Indica que se espera una respuesta remota sin dejar una pantalla vacía. */
@Composable private fun LoadingContent(modifier: Modifier) = Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { CircularProgressIndicator(); Spacer(Modifier.height(12.dp)); Text(stringResource(R.string.loading)) }
/** Ofrece una acción clara cuando aún no existen personas en el servicio. */
@Composable private fun EmptyContent(modifier: Modifier) = Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Icon(Icons.Default.PersonOff, null, modifier = Modifier.size(48.dp)); Spacer(Modifier.height(12.dp)); Text(stringResource(R.string.empty_people)) }
/** Comunica fallos de red o servicio y permite recuperar la operación. */
@Composable private fun ErrorContent(error: String, onRetry: () -> Unit, modifier: Modifier) = Column(modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Icon(Icons.Default.WifiOff, null, modifier = Modifier.size(48.dp)); Text(error.ifBlank { stringResource(R.string.network_error) }); Spacer(Modifier.height(12.dp)); Button(onClick = onRetry) { Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.retry)) } }
