package ge.siradze.multiplayergame.menu.data.network

import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.menu.data.network.api.ServerService
import ge.siradze.multiplayergame.menu.data.network.map.toModel
import ge.siradze.multiplayergame.menu.data.network.model.HostRequestDto
import ge.siradze.multiplayergame.menu.domain.ServerRepository
import ge.siradze.multiplayergame.menu.domain.model.Server

class ServerRepositoryImpl(
    private val serverService: ServerService,
): ServerRepository {
    override suspend fun host(name: String): ResultFace<Server, String> {
        try {
            val result = serverService.host(HostRequestDto(name))
            return when {
                result.isSuccessful -> {
                    result.body()?.server?.let {
                        ResultFace.Success(it.toModel())
                    } ?: ResultFace.Failure("Server is null")
                } else -> {
                    ResultFace.Failure(result.message())
                }
            }
        } catch (e: Exception) {
            return ResultFace.Failure(e.message ?: "Unknown error")
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
                    ResultFace.Failure(result.message())
                }
            }
        } catch (e: Exception) {
            return ResultFace.Failure(e.message ?: "Unknown error")
        }
    }

}