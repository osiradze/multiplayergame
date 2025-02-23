package ge.siradze.multiplayergame.core.network.extensions

import retrofit2.Response


fun <T>Response<Any>.wrap(
    onSuccess : (Any?) -> T,
    onFailure : (String?) -> T
): T {
    return if(!isSuccessful) {
        onSuccess(body())
    } else onFailure(message())
}