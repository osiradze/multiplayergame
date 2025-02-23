package ge.siradze.multiplayergame.menu.data.network.model

import com.google.gson.annotations.SerializedName

data class HostResponseDto (
    @SerializedName("server")
    val server: ServerDto
)