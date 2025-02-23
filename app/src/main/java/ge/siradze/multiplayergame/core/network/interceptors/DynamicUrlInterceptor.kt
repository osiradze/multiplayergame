package ge.siradze.multiplayergame.core.network.interceptors

import ge.siradze.multiplayergame.core.network.BaseUrlProvider
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class DynamicUrlInterceptor(
    private val baseUrlProvider: BaseUrlProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newBaseUrl = HttpUrl.parse(baseUrlProvider.get())

        newBaseUrl?.let {
            val newUrl = originalRequest.url().newBuilder()
                .scheme(newBaseUrl.scheme())
                .host(newBaseUrl.host())
                .port(newBaseUrl.port())
                .build()

            val modifiedRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            return chain.proceed(modifiedRequest)
        }
        return chain.proceed(originalRequest)
    }
}