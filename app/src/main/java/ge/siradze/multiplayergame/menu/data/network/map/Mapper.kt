package ge.siradze.multiplayergame.menu.data.network.map

import ge.siradze.multiplayergame.menu.data.network.model.ServerDto
import ge.siradze.multiplayergame.menu.domain.model.Server

fun List<ServerDto>.toModel() : List<Server> {
    return map { it.toModel() }
}

fun ServerDto.toModel() : Server {
    return Server(
        id = id,
        name = name,
        port = port
    )
}