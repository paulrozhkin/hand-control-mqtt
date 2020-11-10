package com.handcontrol.server.mqtt.command.dto

import com.handcontrol.server.mqtt.command.dto.enums.ModuleTypeWork

/**
 * Telemetry information
 */
@ExperimentalUnsignedTypes
data class Telemetry (
        val telemetryFrequency: UShort,
        val emgStatus: ModuleTypeWork,
        val displayStatus: ModuleTypeWork,
        val gyroStatus: ModuleTypeWork,
        val driverStatus: ModuleTypeWork,
        val lastTimeSync: ULong,
        val EMG: UShort,
        val Gesture: String,
        val power: UByte,
        val pointerFingerPosition: UByte,
        val middleFingerPosition: UByte,
        val ringFinderPosition: UByte,
        val littleFingerPosition: UByte,
        val thumbFingerPosition: UByte
) : java.io.Serializable