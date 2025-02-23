package ge.siradze.multiplayergame.game.data

import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.game.data.socket.ServerSocket
import ge.siradze.multiplayergame.game.domain.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepositoryImpl(
    private val gameService: ServerSocket,
): GameRepository {

    override suspend fun connect(port: Int): ResultFace<Boolean, String> {
        return withContext(Dispatchers.IO) {
            gameService.connect(port)
        }

    }


    override suspend fun send(bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    override suspend fun listen(onReceive: (bytes: ByteArray) -> Unit) {
        withContext(Dispatchers.IO) {
            gameService.listen(onReceive)
        }
    }
}