package ge.siradze.mutiplayergame.core.network

interface BaseUrlProvider {
    fun set(url : String)
    fun get(): String
}

class BaseUrlProviderImpl : BaseUrlProvider {

    private var baseUrl: String = "http://10.178.254.220:8080/"

    override fun set(url : String) {
        baseUrl = url
    }

    override fun get(): String = baseUrl
}