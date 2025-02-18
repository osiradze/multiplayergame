package ge.siradze.mutiplayergame.core.network

import ge.siradze.mutiplayergame.BuildConfig

interface BaseUrlProvider {
    fun set(url : String)
    fun get(): String
}

class BaseUrlProviderImpl : BaseUrlProvider {

    private var baseUrl: String = BuildConfig.BASE_URL


    override fun set(url : String) {
        baseUrl = url
    }

    override fun get(): String = baseUrl
}