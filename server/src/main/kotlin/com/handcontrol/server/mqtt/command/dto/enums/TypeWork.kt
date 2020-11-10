package com.handcontrol.server.mqtt.command.dto.enums

/**
 * Working mode of the prosthesis
 */
@ExperimentalUnsignedTypes
enum class TypeWork(val size: UByte) {
    MIO(0x00u),
    COMMANDS(0x01u),
    AUTO(0x02u)
}