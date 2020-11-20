import emulator.util.StringGenerator
import emulator.util.TelemetryToMsConverter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringGeneratorTest {
    @Test
    fun generateTest() {
        // Given & When
        val length = (5..10).random()
        val convertResult = StringGenerator.generate(length)

        // Then
        assert(convertResult.length == length)
    }
}