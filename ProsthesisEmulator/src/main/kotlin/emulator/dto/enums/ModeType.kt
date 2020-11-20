package emulator.dto.enums

enum class ModeType(val size: Byte) {
    MODE_MIO(0x00),
    MODE_COMMANDS(0x01),
    MODE_AUTO(0x02)
}