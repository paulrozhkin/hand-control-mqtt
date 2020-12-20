import emulator.ControllerEmulator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalUnsignedTypes
suspend fun main(args : Array<String>): Unit = coroutineScope {
    val count: Int = args[0].toInt()
    launch {
        repeat(count) {
            val controller = ControllerEmulator()
            controller.start()
        }
    }
}



