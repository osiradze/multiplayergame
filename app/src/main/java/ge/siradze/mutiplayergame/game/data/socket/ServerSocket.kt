package ge.siradze.mutiplayergame.game.data.socket

import ge.siradze.mutiplayergame.core.ResultFace

interface ServerSocket {
    suspend fun connect(port: Int): ResultFace<Boolean, String>
    suspend fun send(bytes: ByteArray)
    suspend fun listen(onReceive : (bytes: ByteArray) -> Unit)
}