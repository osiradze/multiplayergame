package ge.siradze.multiplayergame.core.network

import ge.siradze.multiplayergame.core.network.interceptors.DynamicUrlInterceptor
import ge.siradze.multiplayergame.menu.data.network.api.ServerService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun provideHttpClient(
    interceptor: DynamicUrlInterceptor
): OkHttpClient {
    return OkHttpClient
        .Builder()
        .addInterceptor(interceptor)
        .readTimeout(2, TimeUnit.SECONDS)
        .connectTimeout(2, TimeUnit.SECONDS)
        .build()
}


fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()


fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .baseUrl("https://www.google.com/")
        .build()
}

fun provideServerService(retrofit: Retrofit): ServerService = retrofit.create(ServerService::class.java)
