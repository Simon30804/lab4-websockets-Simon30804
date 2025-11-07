@file:Suppress("NoWildcardImports")

package websockets

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.ContainerProvider
import jakarta.websocket.OnMessage
import jakarta.websocket.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ElizaServerTest {
    @LocalServerPort
    private var port: Int = 0

    @Test
    fun onOpen() {
        logger.info { "This is the test worker" }
        val latch = CountDownLatch(3)
        val list = mutableListOf<String>()

        val client = SimpleClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        assertEquals(3, list.size)
        assertEquals("The doctor is in.", list[0])
    }

    @Test
    fun onChat() {
        logger.info { "Test thread" }
        val latch = CountDownLatch(4)
        val list = mutableListOf<String>()

        val client = ComplexClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        val size = list.size
        // 1. EXPLAIN WHY size = list.size IS NECESSARY
        // En caso de que haya hilos concurrentes que modifiquen la lista después del latch.await(), puede que el tamaño de la lista cambie.
        // es por ello que capturamos el tamaño en una variable local para evitar inconsistencias y asegurarnos que el tamaño no cambie entre la comprobación y el acceso a los elementos.
        // 2. REPLACE BY assertXXX expression that checks an interval; assertEquals must not be used;
        // El rango esperado es entre 4 y 5 mensajes recibidos:
        //   mínimo 3 mensajes iniciales más 1 mensaje de respuesta, y existe la posibilidad de recibir un mensaje adicional por variación temporal con el servidor.
        assertTrue(size in 4..5, "El tamaño de la lista debe estar entre 4 y 5, pero es $size")
        // 3. EXPLAIN WHY assertEquals CANNOT BE USED AND WHY WE SHOULD CHECK THE
        // En este caso no podemos usar assertEquals porque el número exacto de mensajes recibidos puede variar dependiendo de la interacción con el servidor,
        // podríamos recibir 4 o 5 mensajes dependiendo de cómo responda el servidor a nuestro mensaje, por eso verificamos un intervalo y no
        // un valor exacto.
        // 4. COMPLETE assertEquals(XXX, list[XXX])
        assertEquals("The doctor is in.", list[0])
    }
}

@ClientEndpoint
class SimpleClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(message: String) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
    }
}

@ClientEndpoint
class ComplexClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(
        message: String,
        session: Session,
    ) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
        // 5. COMPLETE if (expression) {
        // 6. COMPLETE   sentence
        // }
        // Cuando recibimos el mensaje inicial del doctor, enviamos un mensaje al servidor
        if (message == "The doctor is in.") {
            session.basicRemote.sendText("I am feeling sad.")
        }
    }
}

fun Any.connect(uri: String) {
    ContainerProvider.getWebSocketContainer().connectToServer(this, URI(uri))
}
