package ge.siradze.multiplayergame.core.network

import ge.siradze.multiplayergame.BuildConfig
import java.net.URI

interface BaseUrlProvider {
    fun set(url : String)
    fun get(): String
    fun getWithoutPort(): String
}

class BaseUrlProviderImpl : BaseUrlProvider {

    private var baseUrl: String = BuildConfig.BASE_URL


    override fun set(url : String) {
        baseUrl = url
    }

    override fun get(): String {
        return baseUrl
    }

    override fun getWithoutPort(): String {
        val uri = URI(baseUrl)
        return uri.host
    }
}