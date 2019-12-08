package net.tomasfiers.zoro.util

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Wrapper to encapsulate OkHttp / Retrofit / Moshi syntax.
// Call as `createJsonHttpClient(..., MyInterface::class.java)`
fun <T> createJsonHttpClient(
    baseUrl: String,
    requestHeaders: Map<String, String>,
    APIInterface: Class<T>
): T {
    val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            for ((k, v) in requestHeaders) {
                requestBuilder.addHeader(k, v)
            }
            chain.proceed(requestBuilder.build())
        }
        .build()
    val jsonParser = Moshi.Builder()
        .build()!!
    val jsonConverterFactory = MoshiConverterFactory.create(jsonParser)
    return Retrofit.Builder()
        .client(httpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create(APIInterface)
}