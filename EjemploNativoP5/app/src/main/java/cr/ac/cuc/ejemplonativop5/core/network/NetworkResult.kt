package cr.ac.cuc.ejemplonativop5.core.network

/** Resultado uniforme de una operación remota para evitar que los errores HTTP lleguen a la interfaz. */
sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error(val message: String? = null, val code: Int? = null) : NetworkResult<Nothing>
}
