import emulator.util.TelemetryToMsConverter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TelemetryConverterTest {
    @Test
    fun convertTest() {
        // Given
        val telemetry: Short = 250
        val expectedResult: Long = 4

        // When
        val convertResult = TelemetryToMsConverter.convert(telemetry)

        // Then
        assertEquals(expectedResult, convertResult)
    }
}