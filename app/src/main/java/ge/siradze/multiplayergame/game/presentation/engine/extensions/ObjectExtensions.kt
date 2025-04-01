package ge.siradze.multiplayergame.game.presentation.engine.extensions


fun FloatArray.scale(times: Float, floatsPerVertex: Int): FloatArray {
    return FloatArray(
        init = { i ->
            if(i % floatsPerVertex == 0 || i % floatsPerVertex == 1) {
                get(i) * times
            } else {
                get(i)
            }
        },
        size = size

    )
}

fun FloatArray.transform(x: Float, y: Float, floatsPerVertex: Int): FloatArray {
    return FloatArray(
        init = { i ->
            if(i % floatsPerVertex == 0) {
                get(i) + x
            } else if (i % floatsPerVertex == 1)  {
                get(i) + y
            } else {
                get(i)
            }
        },
        size = size
    )
}

fun FloatArray.middlePoint(floatsPerVertex: Int): FloatArray {

    val vertexNumber = size / floatsPerVertex

    var midX = 0f
    var midY = 0f

    for (i in indices) {
        if (i % floatsPerVertex != 0) {
            midX += get(i)
        }
        if (i % floatsPerVertex != 1) {
            midY += get(i + 1)
        }

    }
    return floatArrayOf(midX / vertexNumber, midY / vertexNumber)
}