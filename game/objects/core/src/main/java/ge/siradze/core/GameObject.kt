package ge.siradze.core

interface GameObject {

    fun init()

    fun setRatio(ratio: Float) = Unit

    fun onSizeChange(width: Int, height: Int) = Unit

    fun draw()

    fun release()

}