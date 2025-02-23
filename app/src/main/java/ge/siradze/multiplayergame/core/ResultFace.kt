package ge.siradze.multiplayergame.core

sealed class ResultFace<T, R> {
    data class Success<T, R>(val value: T) : ResultFace<T, R>()
    data class Failure<R, T>(val error: R) : ResultFace<T, R>()
}

