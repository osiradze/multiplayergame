package ge.siradze.glcore

interface GameState {

    fun set(key: String?, value: Any)
    fun get(key: String?): Any?
}