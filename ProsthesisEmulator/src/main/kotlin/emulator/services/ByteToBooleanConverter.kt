package emulator.services

@ExperimentalUnsignedTypes
class ByteToBooleanConverter() {
    companion object Converter {
        fun convert(byte: UByte): Boolean {
            return when (byte) {
                0.toUByte() -> false
                else -> true
            }
        }

        fun convertBack(value: Boolean): UByte {
            return if (value) {
                1u
            } else {
                0u
            }
        }
    }
}