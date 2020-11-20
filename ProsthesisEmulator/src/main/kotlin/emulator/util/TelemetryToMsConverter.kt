package emulator.util

class TelemetryToMsConverter {
    companion object {
        fun convert(telemetryFrequency: Short): Long {
            return ((1.0 / telemetryFrequency) * 1000).toLong()
        }
    }
}