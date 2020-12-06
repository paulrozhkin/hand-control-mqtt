package com.handcontrol.server.service

import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import com.handcontrol.server.service.dao.GestureDtoRepository
import org.springframework.stereotype.Service

@Service
class GestureDtoServiceImpl(val repository: GestureDtoRepository): GestureDtoService {

    override fun getGesturesById(id: String): GestureDto {
        return repository.findById(id).get()
    }

    override fun getAllGestures(): List<GestureDto> {
        return repository.findAll().toList()
    }

    override fun save(gesture: GestureDto): GestureDto {
        return repository.save(gesture)
    }

    override fun delete(id: String) {
        return repository.deleteById(id)
    }
}