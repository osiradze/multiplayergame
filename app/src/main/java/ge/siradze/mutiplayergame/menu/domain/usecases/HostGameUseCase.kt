package ge.siradze.mutiplayergame.menu.domain.usecases

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.menu.domain.ServerRepository
import ge.siradze.mutiplayergame.menu.domain.model.Server

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