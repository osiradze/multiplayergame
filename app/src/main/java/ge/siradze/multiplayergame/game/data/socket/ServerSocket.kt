package ge.siradze.multiplayergame.game.data.socket

import ge.siradze.multiplayergame.core.ResultFace

interface ServerSocket {
    suspend fun connect(port: Int): ResultFace<Boolean, String>
    suspend fun send(bytes: ByteArray)
    suspend fun listen(onReceive : (bytes: ByteArray) -> Unit)
}