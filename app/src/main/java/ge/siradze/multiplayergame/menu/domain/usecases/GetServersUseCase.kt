package ge.siradze.multiplayergame.menu.domain.usecases

import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.menu.domain.ServerRepository
import ge.siradze.multiplayergame.menu.domain.model.Server

fun interface GetServersUseCase {
    suspend fun invoke(): ResultFace<List<Server>, String>
}

class GetServersUseCaseImpl(
    private val serverRepository: ServerRepository
) : GetServersUseCase {
    override suspend fun invoke(): ResultFace<List<Server>, String> {
        val result = serverRepository.getServers()
        return result
    }

}