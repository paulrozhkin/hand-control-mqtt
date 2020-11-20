package emulator.dto.enums

@ExperimentalUnsignedTypes
enum class DriverStatusType(val size: UByte) {
    DRIVER_STATUS_INITIALIZATION(0x00u),
    DRIVER_STATUS_ERROR(0x02u),
    DRIVER_STATUS_CONNECTION_ERROR(0x03u),
    DRIVER_STATUS_DISABLED(0x04u),
    DRIVER_STATUS_SLEEP(0xF1u),
    DRIVER_STATUS_SETTING_POSITION(0xF2u)
}