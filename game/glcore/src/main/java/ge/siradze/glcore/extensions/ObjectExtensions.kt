package ge.siradze.glcore.extensions


fun FloatArray.scale(times: Float, floatsPerVertex: Int) {
    for (i in indices) {
        if (i % floatsPerVertex == 0 || i % floatsPerVertex == 1) {
            set(i, this[i] * times)
        }
    }
}

fun FloatArray.transform(x: Float, y: Float, floatsPerVertex: Int) {
    for (i in indices) {
        if (i % floatsPerVertex == 0) {
            set(i, this[i] + x)
        } else if (i % floatsPerVertex == 1) {
            set(i, this[i] + y)
        }
    }
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