package com.handcontrol.server.mqtt.command.dto

import com.handcontrol.server.protobuf.Enums.DriverStatusType
import com.handcontrol.server.protobuf.Enums.ModuleStatusType
import com.handcontrol.server.protobuf.TelemetryOuterClass
import kotlinx.serialization.Serializable

/**
 * Telemetry information
 */
@Serializable
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
) {
    companion object {
        fun createFrom(from: TelemetryDto): TelemetryOuterClass.Telemetry {
            return TelemetryOuterClass.Telemetry.newBuilder()
                .setTelemetryFrequency(from.telemetryFrequency)
                .setEmgStatus(from.emgStatus)
                .setDisplayStatus(from.displayStatus)
                .setGyroStatus(from.gyroStatus)
                .setDriverStatus(from.driverStatus)
                .setLastTimeSync(from.lastTimeSync)
                .setEmg(from.emg)
                .setExecutableGesture(UuidDto.createFrom(from.executableGesture))
                .setPower(from.power)
                .setPointerFingerPosition(from.pointerFingerPosition)
                .setMiddleFingerPosition(from.middleFingerPosition)
                .setRingFingerPosition(from.ringFinderPosition)
                .setLittleFingerPosition(from.littleFingerPosition)
                .setThumbFingerPosition(from.thumbFingerPosition)
                .build()
        }
    }
}
