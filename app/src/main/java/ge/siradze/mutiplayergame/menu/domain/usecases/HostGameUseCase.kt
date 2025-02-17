package ge.siradze.mutiplayergame.menu.domain.usecases

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.menu.data.network.ServerRepository

fun interface HostGameUseCase {
    suspend fun invoke(name: String): ResultFace<Int, String>
}

class HostGameUseCaseImpl(
    private val serverRepository: ServerRepository
) : HostGameUseCase {
    override suspend fun invoke(name: String): ResultFace<Int, String> {
        return serverRepository.host(name)
    }

}