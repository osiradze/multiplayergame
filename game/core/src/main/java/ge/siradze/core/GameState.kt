package ge.siradze.core

interface GameState {

    fun set(key: String?, value: Any)
    fun get(key: String?): Any?
}