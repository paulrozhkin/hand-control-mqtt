import emulator.models.SetSettings
import emulator.models.enums.TypeWork
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class SerializeTests {


    @Test
    fun setSettingsTest() {
        // Given
        val settings = SetSettings(TypeWork.COMMANDS, 400u, enableEmg = false,
                enableDisplay = true, enableGyro = true,
                enableDriver = false)

        val expectedResult = ubyteArrayOf(0x01u, 0x90u, 0x01u, 0u, 1u, 1u, 0u).toTypedArray()

        // When
        val byteArray = settings.serialize();

        // Then
        assert(expectedResult.contentEquals(byteArray.toTypedArray()))
    }
}