package ge.siradze.multiplayergame.game.presentation

import ge.siradze.core.GameState

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