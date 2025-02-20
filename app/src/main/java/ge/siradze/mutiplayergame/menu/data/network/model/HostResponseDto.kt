package ge.siradze.mutiplayergame.menu.data.network.model

import com.google.gson.annotations.SerializedName
import ge.siradze.mutiplayergame.menu.domain.model.Server

data class HostResponseDto (
    @SerializedName("server")
    val server: ServerDto
)