package emulator.models.enums

@ExperimentalUnsignedTypes
enum class ModuleTypeWorkEnum(val value: UByte) {
    InitializationMode(0x00.toUByte()),
    Work(0x01.toUByte()),
    ErrorMode(0x02.toUByte()),
    ConnectionError(0x03.toUByte()),
    ModuleDisabled(0x04.toUByte())
}