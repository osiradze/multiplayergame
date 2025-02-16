package ge.siradze.mutiplayergame.menu.domain.usecases

import ge.siradze.mutiplayergame.core.ResultFace

fun interface HostGameUseCase {
    suspend fun invoke(): ResultFace<Int, Unit>
}

class HostGameUseCaseImpl : HostGameUseCase {
    override suspend fun invoke(): ResultFace<Int, Unit> {
        return ResultFace.Success(5000)
    }

}