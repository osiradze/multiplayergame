package ge.siradze.mutiplayergame.menu.data.network

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.menu.domain.model.Server

interface ServerRepository {
    fun host()
    suspend fun getServers(): ResultFace<List<Server>, String>
}