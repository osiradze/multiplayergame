package ge.siradze.mutiplayergame.core.network

interface BaseUrlProvider {
    fun set(url : String)
    fun get(): String
}

class BaseUrlProviderImpl : BaseUrlProvider {

    private var baseUrl: String = "192.168.25.211"

    override fun set(url : String) {
        baseUrl = url
    }

    override fun get(): String = baseUrl
}