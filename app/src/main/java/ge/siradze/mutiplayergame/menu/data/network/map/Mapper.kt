package ge.siradze.mutiplayergame.menu.data.network.map

import ge.siradze.mutiplayergame.menu.data.network.model.ServerDto
import ge.siradze.mutiplayergame.menu.domain.model.Server

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