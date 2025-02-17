package ge.siradze.mutiplayergame.menu.data.network

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.core.network.BaseUrlProvider
import ge.siradze.mutiplayergame.menu.data.network.api.ServerService
import ge.siradze.mutiplayergame.menu.data.network.map.toModel
import ge.siradze.mutiplayergame.menu.data.network.model.HostRequestDto
import ge.siradze.mutiplayergame.menu.domain.model.Server
import kotlinx.coroutines.Dispatchers

class ServerRepositoryImpl(
    private val serverService: ServerService,
): ServerRepository {
    override suspend fun host(name: String): ResultFace<Int, String> {
        try {
            val result = serverService.host(HostRequestDto(name))
            return when {
                result.isSuccessful -> {
                    result.body()?.serverId?.let {
                        ResultFace.Success(it)
                    } ?: ResultFace.Error("Server id is null")
                } else -> {
                    ResultFace.Error(result.message())
                }
            }
        } catch (e: Exception) {
            return ResultFace.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getServers(): ResultFace<List<Server>, String> {
        try {
            val result = serverService.getServers()
            return when {
                result.isSuccessful -> {
                    ResultFace.Success(
                        result.body()?.map { it.toModel() } ?: emptyList()
                    )
                } else -> {
                    ResultFace.Error(result.message())
                }
            }
        } catch (e: Exception) {
            return ResultFace.Error(e.message ?: "Unknown error")
        }
    }

}