package com.example.financlick_movilwear.presentation.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://192.168.137.1:5000/api/"
    // REMOTE CONTROL LOCALHOST
    //private const val BASE_URL = "http://10.0.2.2:5000/api/"

    private var token: String? = null

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .sslSocketFactory(createUnsafeSslSocketFactory(), createTrustManager())
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    private fun createUnsafeSslSocketFactory(): javax.net.ssl.SSLSocketFactory {
        val trustAllCertificates = arrayOf<javax.net.ssl.TrustManager>(createTrustManager())
        val sslContext = javax.net.ssl.SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCertificates, java.security.SecureRandom())
        return sslContext.socketFactory
    }

    private fun createTrustManager(): javax.net.ssl.X509TrustManager {
        return object : javax.net.ssl.X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? = arrayOf()
        }
    }

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    fun setToken(newToken: String) {
        token = newToken
    }
}