package ge.siradze.mutiplayergame.menu.data.network

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.menu.domain.model.Server

interface ServerRepository {
    suspend fun host(name: String): ResultFace<Int, String>
    suspend fun getServers(): ResultFace<List<Server>, String>
}