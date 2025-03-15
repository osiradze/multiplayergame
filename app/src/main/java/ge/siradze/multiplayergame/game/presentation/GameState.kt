package ge.siradze.multiplayergame.game.presentation

interface GameState {

    fun set(key: String?, value: Any)
    fun get(key: String?): Any?
}

class GameStateImpl : GameState {
    private val map: MutableMap<String, Any> = mutableMapOf()

    override fun set(key: String?, value: Any) {
        if (key == null) return
        map[key] = value
    }
    override fun get(key: String?): Any? {
        return map[key]
    }
}