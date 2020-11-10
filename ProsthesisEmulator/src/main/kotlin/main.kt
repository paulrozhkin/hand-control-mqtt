import emulator.ControllerEmulator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@ExperimentalUnsignedTypes
suspend fun main(): Unit = coroutineScope {

    launch {
        repeat(1) {
            val controller = ControllerEmulator()
            controller.start()
        }
    }
}



