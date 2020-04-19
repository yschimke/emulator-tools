package ee.schimke.emulatortools

import com.android.emulator.control.EmulatorControllerGrpcKt
import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import java.io.Closeable
import java.util.concurrent.TimeUnit

class EmulatorController(val channel: ManagedChannel) : Closeable {
    val stub = EmulatorControllerGrpcKt.EmulatorControllerCoroutineStub(channel)

    suspend fun status() {
        try {
            val response = stub.getStatus(Empty.getDefaultInstance())
            println("Greeter client received: ${response.version}")
        } catch (e: StatusException) {
            println("RPC failed: ${e.status}")
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

suspend fun main() {
    val c = ManagedChannelBuilder.forTarget("localhost:8554").usePlaintext().build()

    val client = EmulatorController(c)
    client.status()
    println(client.stub.getBattery(Empty.getDefaultInstance()))
}
