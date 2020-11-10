package emulator.models.enums

@ExperimentalUnsignedTypes
enum class DriverTypeWorkEnum(val value: UByte) {
    InitializationMode(0x00.toUByte()),
    ErrorMode(0x02.toUByte()),
    ConnectionError(0x03.toUByte()),
    ModuleDisabled(0x04.toUByte()),
    SleepMode(0xF1.toUByte()),
    SettingPositionMode(0xF2.toUByte())
}