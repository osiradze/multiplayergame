package ge.siradze.multiplayergame.menu.domain.usecases

import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.menu.domain.ServerRepository
import ge.siradze.multiplayergame.menu.domain.model.Server

fun interface HostGameUseCase {
    suspend fun invoke(name: String): ResultFace<Server, String>
}

class HostGameUseCaseImpl(
    private val serverRepository: ServerRepository
) : HostGameUseCase {
    override suspend fun invoke(name: String): ResultFace<Server, String> {
        return serverRepository.host(name)
    }

}