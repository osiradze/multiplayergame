package ge.siradze.mutiplayergame.menu.data.network.model

import com.google.gson.annotations.SerializedName

data class HostResponseDto (
    @SerializedName("serverId")
    val serverId: Int
)