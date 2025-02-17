package ge.siradze.mutiplayergame.menu.data.network.model

import com.google.gson.annotations.SerializedName

data class HostRequestDto (
    @SerializedName("name")
    val name: String
)