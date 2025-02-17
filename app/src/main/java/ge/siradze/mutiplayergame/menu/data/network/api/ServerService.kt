package ge.siradze.mutiplayergame.menu.data.network.api


import ge.siradze.mutiplayergame.menu.data.network.model.ServerDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ServerService {
    @GET("/servers")
    suspend fun getServers(): Response<List<ServerDto>>
}