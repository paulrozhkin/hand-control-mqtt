package emulator.dto

import emulator.dto.enums.DriverStatusType
import emulator.dto.enums.ModuleStatusType
import kotlinx.serialization.Serializable

/**
 * Telemetry information
 */
@Serializable
@ExperimentalUnsignedTypes
data class TelemetryDto(
        val telemetryFrequency: Int = 0,
        val emgStatus: ModuleStatusType = ModuleStatusType.MODULE_STATUS_INITIALIZATION,
        val displayStatus: ModuleStatusType = ModuleStatusType.MODULE_STATUS_INITIALIZATION,
        val gyroStatus: ModuleStatusType = ModuleStatusType.MODULE_STATUS_INITIALIZATION,
        val driverStatus: DriverStatusType = DriverStatusType.DRIVER_STATUS_INITIALIZATION,
        val lastTimeSync: Long = 0,
        val emg: Int = 0,
        val executableGesture: UuidDto = UuidDto(""),
        val power: Int = 0,
        val pointerFingerPosition: Int = 0,
        val middleFingerPosition: Int = 0,
        val ringFinderPosition: Int = 0,
        val littleFingerPosition: Int = 0,
        val thumbFingerPosition: Int = 0
)