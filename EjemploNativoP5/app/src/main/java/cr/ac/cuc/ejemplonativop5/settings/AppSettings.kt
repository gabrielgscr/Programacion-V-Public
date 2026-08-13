package cr.ac.cuc.ejemplonativop5.settings

import android.app.Activity
import android.content.Context

/** Guarda las preferencias de presentación y aplica el idioma elegido a toda la aplicación. */
class AppSettings(context: Context) {
    private val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    var themeMode: ThemeMode
        get() = ThemeMode.valueOf(preferences.getString("theme", ThemeMode.SYSTEM.name)!!)
        set(value) = preferences.edit().putString("theme", value.name).apply()

    fun setLanguage(languageTag: String, activity: Activity) {
        preferences.edit().putString("language", languageTag).apply()
        activity.recreate()
    }

    fun language(): String = preferences.getString("language", "")!!
}

/** Opciones de tema que el usuario puede seleccionar en la configuración. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }
