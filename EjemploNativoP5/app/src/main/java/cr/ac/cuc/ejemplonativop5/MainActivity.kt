package cr.ac.cuc.ejemplonativop5

import android.os.Bundle
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import cr.ac.cuc.ejemplonativop5.di.AppContainer
import cr.ac.cuc.ejemplonativop5.ui.people.PersonasRoute
import cr.ac.cuc.ejemplonativop5.ui.people.PersonasViewModel
import cr.ac.cuc.ejemplonativop5.ui.people.PersonasViewModelFactory
import cr.ac.cuc.ejemplonativop5.ui.theme.EjemploNativoP5Theme
import cr.ac.cuc.ejemplonativop5.settings.AppSettings
import cr.ac.cuc.ejemplonativop5.settings.ThemeMode

/** Activity principal que configura las dependencias y muestra el mantenimiento de personas. */
class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val language = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE).getString("language", "").orEmpty()
        if (language.isBlank()) {
            super.attachBaseContext(newBase)
            return
        }
        val configuration = Configuration(newBase.resources.configuration).apply {
            setLocales(LocaleList.forLanguageTags(language))
        }
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val container = remember { AppContainer() }
            val settings = remember { AppSettings(this) }
            var themeMode by remember { mutableStateOf(settings.themeMode) }
            val viewModel: PersonasViewModel = viewModel(
                factory = PersonasViewModelFactory(container.personaRepository)
            )

            val darkTheme = when (themeMode) { ThemeMode.SYSTEM -> isSystemInDarkTheme(); ThemeMode.LIGHT -> false; ThemeMode.DARK -> true }
            EjemploNativoP5Theme(darkTheme = darkTheme) {
                PersonasRoute(viewModel = viewModel, settings = settings, onThemeChanged = { themeMode = it })
            }
        }
    }
}
