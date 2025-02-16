package ge.siradze.mutiplayergame.menu.data.network

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.core.network.BaseUrlProvider
import ge.siradze.mutiplayergame.menu.data.network.api.ServerService
import ge.siradze.mutiplayergame.menu.data.network.map.toModel
import ge.siradze.mutiplayergame.menu.domain.model.Server

class ServerRepositoryImpl(
    private val baseUrlProvider: BaseUrlProvider,
    private val serverService: ServerService
): ServerRepository {
    override fun host() {
        TODO("Not yet implemented")
    }

    override fun getServers(): ResultFace<List<Server>, String> {
        val result = serverService.getServers(baseUrlProvider.get()).execute()
        return when {
            result.isSuccessful -> {
                 ResultFace.Success(
                    result.body()?.map { it.toModel() } ?: emptyList()
                )
            }
            else -> {
                ResultFace.Error(result.message())
            }
        }

    }

}