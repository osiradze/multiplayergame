package ge.siradze.mutiplayergame.core

sealed class ResultFace<T, R> {
    data class Success<T, R>(val value: T) : ResultFace<T, R>()
    data class Error<R, T>(val error: R) : ResultFace<T, R>()
}

