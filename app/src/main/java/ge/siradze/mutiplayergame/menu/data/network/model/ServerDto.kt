package ge.siradze.mutiplayergame.menu.data.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServerDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("port") val port: Int,
) : Serializable