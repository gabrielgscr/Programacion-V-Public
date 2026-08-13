package cr.ac.cuc.ejemplonativop5.di

import cr.ac.cuc.ejemplonativop5.BuildConfig
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/** Crea el cliente HTTP y limita la aceptación del certificado local no confiable a compilaciones debug. */
object NetworkClientFactory {
    fun create(): OkHttpClient {
        if (!BuildConfig.DEBUG || !BuildConfig.PERSONA_API_BASE_URL.contains("10.0.2.2")) {
            return OkHttpClient.Builder().build()
        }

        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        }
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .build()
    }
}
