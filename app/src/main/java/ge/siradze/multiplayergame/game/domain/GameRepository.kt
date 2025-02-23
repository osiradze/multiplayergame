package ge.siradze.multiplayergame.game.domain

import ge.siradze.multiplayergame.core.ResultFace

interface GameRepository {
    suspend fun connect(port: Int): ResultFace<Boolean, String>
    suspend fun send(bytes: ByteArray)
    suspend fun listen(onReceive : (bytes: ByteArray) -> Unit)
}