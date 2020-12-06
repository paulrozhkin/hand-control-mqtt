package com.handcontrol.server.mqtt.command.dto

import com.handcontrol.server.protobuf.Uuid
import kotlinx.serialization.Serializable

/**
 * A prosthesis identifier
 */
@Serializable
data class UuidDto(val value: String = "") {
    companion object {
        fun createFrom(from: Uuid.UUID) : UuidDto {
            return UuidDto(value = from.value)
        }
    }
}