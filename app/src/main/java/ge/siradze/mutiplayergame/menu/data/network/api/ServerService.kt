package ge.siradze.mutiplayergame.menu.data.network.api


import ge.siradze.mutiplayergame.menu.data.network.model.ServerDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ServerService {
    @GET
    fun getServers(@Url url: String?): Call<List<ServerDto>>
}