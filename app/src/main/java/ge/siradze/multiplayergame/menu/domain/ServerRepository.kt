package ge.siradze.multiplayergame.menu.domain

import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.menu.domain.model.Server

interface ServerRepository {
    suspend fun host(name: String): ResultFace<Server, String>
    suspend fun getServers(): ResultFace<List<Server>, String>
}