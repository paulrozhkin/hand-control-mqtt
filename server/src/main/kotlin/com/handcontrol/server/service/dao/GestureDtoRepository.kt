package com.handcontrol.server.service.dao

import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


//@Repository
interface GestureDtoRepository: CrudRepository<GestureDto, String>