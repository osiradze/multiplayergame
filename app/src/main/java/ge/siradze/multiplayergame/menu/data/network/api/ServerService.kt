package ge.siradze.multiplayergame.menu.data.network.api


import ge.siradze.multiplayergame.menu.data.network.model.HostRequestDto
import ge.siradze.multiplayergame.menu.data.network.model.HostResponseDto
import ge.siradze.multiplayergame.menu.data.network.model.ServerDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerService {
    @GET("/servers")
    suspend fun getServers(): Response<List<ServerDto>>

    @POST("/host")
    suspend fun host(@Body request: HostRequestDto): Response<HostResponseDto>
}