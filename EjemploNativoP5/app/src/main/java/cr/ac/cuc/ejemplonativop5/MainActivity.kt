package cr.ac.cuc.ejemplonativop5

import android.os.Bundle
import android.os.Build
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    private var localNetworkAllowed by mutableStateOf(false)
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        localNetworkAllowed = granted
    }

    private fun needsLocalNetworkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 37) return false
        val host = Uri.parse(BuildConfig.PERSONA_API_BASE_URL).host.orEmpty()
        val octets = host.split('.').mapNotNull { it.toIntOrNull() }
        return host == "localhost" || host == "::1" || host.endsWith(".local") ||
            (octets.size == 4 && (octets[0] == 10 || octets[0] == 127 ||
                (octets[0] == 192 && octets[1] == 168) ||
                (octets[0] == 172 && octets[1] in 16..31) ||
                (octets[0] == 169 && octets[1] == 254)))
    }

    override fun onResume() {
        super.onResume()
        localNetworkAllowed = !needsLocalNetworkPermission() ||
            checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK) == PackageManager.PERMISSION_GRANTED
    }

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
            val darkTheme = when (themeMode) { ThemeMode.SYSTEM -> isSystemInDarkTheme(); ThemeMode.LIGHT -> false; ThemeMode.DARK -> true }
            EjemploNativoP5Theme(darkTheme = darkTheme) {
                LaunchedEffect(Unit) {
                    if (needsLocalNetworkPermission() &&
                        checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK) != PackageManager.PERMISSION_GRANTED) {
                        permissionRequest.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
                    }
                }
                if (localNetworkAllowed) {
                    val viewModel: PersonasViewModel = viewModel(
                        factory = PersonasViewModelFactory(container.personaRepository)
                    )
                    PersonasRoute(viewModel = viewModel, settings = settings, onThemeChanged = { themeMode = it })
                } else {
                    Surface(Modifier.fillMaxSize()) {
                        Column(
                            Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.local_network_permission_message))
                            Button(onClick = { permissionRequest.launch(Manifest.permission.ACCESS_LOCAL_NETWORK) }) {
                                Text(stringResource(R.string.local_network_permission_allow))
                            }
                            Button(onClick = {
                                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
                            }) {
                                Text(stringResource(R.string.local_network_permission_settings))
                            }
                        }
                    }
                }
            }
        }
    }
}
