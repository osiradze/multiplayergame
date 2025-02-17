package ge.siradze.mutiplayergame.menu.domain.usecases

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.menu.data.network.ServerRepository
import ge.siradze.mutiplayergame.menu.domain.model.Server

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