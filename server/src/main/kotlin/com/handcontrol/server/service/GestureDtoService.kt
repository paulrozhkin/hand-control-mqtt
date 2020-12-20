package com.handcontrol.server.service

import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import org.springframework.stereotype.Service

@Service
interface GestureDtoService {

    fun getGesturesById(id: String): GestureDto

    fun getAllGestures(): List<GestureDto>

    fun save(gesture: GestureDto): GestureDto

    fun delete(id: String)
}