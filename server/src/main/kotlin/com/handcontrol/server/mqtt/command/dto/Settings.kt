package com.handcontrol.server.mqtt.command.dto

/**
 * A prosthesis identifier
 */
data class Settings(
        val typeWork: UByte,
        val freq: UShort,
        val emg: Boolean,
        val disp: Boolean,
        val gyro: Boolean,
        val driver: Boolean,
        val enable: Boolean
) : java.io.Serializable