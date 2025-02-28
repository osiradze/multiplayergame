package ge.siradze.multiplayergame.game.presentation.engine.objects

interface GameObject {

    fun init()

    fun setRatio(ratio: Float) = Unit

    fun draw()

    fun release()

}