package com.handcontrol.server.mqtt.command.dto.enums

enum class ModuleStatusType(val size: Byte) {
    MODULE_STATUS_INITIALIZATION(0x00),
    MODULE_STATUS_WORK(0x01),
    MODULE_STATUS_ERROR(0x02),
    MODULE_STATUS_CONNECTION_ERROR(0x03),
    MODULE_STATUS_DISABLED(0x04)
}