package com.handcontrol.server.mqtt.command.dto.enums

/**
 * Working mode of the Module
 */
@ExperimentalUnsignedTypes
enum class ModuleTypeWork(val size: UByte) {
    INIT(0x00u),
    WORK(0x01u),
    ERR(0x02u),
    CONNECTION_ERR(0x03u),
    DISABLED(0x04u)
}