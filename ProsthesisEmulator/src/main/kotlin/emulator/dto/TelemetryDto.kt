package emulator.dto

import emulator.enums.DriverStatusType
import emulator.enums.ModuleStatusType
import kotlinx.serialization.Serializable

/**
 * Telemetry information
 */
@Serializable
@ExperimentalUnsignedTypes
data class TelemetryDto(
        val telemetryFrequency: Short = 0,
        val emgStatus: ModuleStatusType = ModuleStatusType.MODULE_STATUS_INITIALIZATION,
        val displayStatus: ModuleStatusType = ModuleStatusType.MODULE_STATUS_INITIALIZATION,
        val gyroStatus: ModuleStatusType = ModuleStatusType.MODULE_STATUS_INITIALIZATION,
        val driverStatus: DriverStatusType = DriverStatusType.DRIVER_STATUS_INITIALIZATION,
        val lastTimeSync: Long = 0,
        val emg: Short = 0,
        val executableGesture: UuidDto = UuidDto(""),
        val power: Byte = 0,
        val pointerFingerPosition: Byte = 0,
        val middleFingerPosition: Byte = 0,
        val ringFinderPosition: Byte = 0,
        val littleFingerPosition: Byte = 0,
        val thumbFingerPosition: Byte = 0
)