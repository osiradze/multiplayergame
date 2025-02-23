package ge.siradze.mutiplayergame.game.data.socket

import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.core.network.BaseUrlProvider
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ConnectedDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeByteBuffer
import io.ktor.utils.io.core.writeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.io.readByteArray
import java.nio.ByteBuffer
import kotlin.coroutines.coroutineContext

class ServerSocketImpl(
    private val baseUrlProvider: BaseUrlProvider
) : ServerSocket {
    companion object {
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
    }

    private var socket: ConnectedDatagramSocket? = null

    override suspend fun connect(port: Int): ResultFace<Boolean, String> {
        try {
            socket = aSocket(selectorManager).udp().connect(
                InetSocketAddress(baseUrlProvider.getWithoutPort(), port)
            )
            greet()
            return ResultFace.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            return ResultFace.Failure(e.message ?: "Unknown error")
        }
    }

    private suspend fun greet() {
        val socket = socket ?: return
        val packet = buildPacket {
            writeText("Hello")
        }
        socket.send(
            Datagram(packet, socket.remoteAddress)
        )
    }

    override suspend fun send(bytes: ByteArray) {
        val socket = socket ?: return

        val packet = buildPacket {
            writeByteBuffer(ByteBuffer.wrap(bytes))
        }
        socket.send(
            Datagram(packet, socket.remoteAddress)
        )
    }

    override suspend fun listen(onReceive: (bytes: ByteArray) -> Unit) {
        val socket = socket ?: return
        while (coroutineContext.isActive) {

            socket.receive().packet.readByteArray().let(onReceive)
        }
    }
}