import emulator.models.SetSettings
import emulator.models.enums.TypeWork
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class DeserializeTests {

    @Test
    fun setSettingsTest() {
        // Given
        val expectedResult = SetSettings(TypeWork.COMMANDS, 400u, enableEmg = false,
                enableDisplay = true, enableGyro = true,
                enableDriver = false)

        val settingsByteArray = ubyteArrayOf(0x01u, 0x90u, 0x01u, 0u, 1u, 1u, 0u)

        // When
        val byteArray = SetSettings.deserialize(settingsByteArray)

        // Then
        assertEquals(expectedResult, byteArray)
    }
}