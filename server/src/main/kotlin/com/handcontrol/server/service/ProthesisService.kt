package com.handcontrol.server.service

import com.handcontrol.server.entity.Prothesis
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import java.util.*

interface ProthesisService {

    fun getProthesisById(id: String): Optional<Prothesis>

    fun getAllOnlineProtheses(): List<Prothesis>

    fun save(prothesis: Prothesis): Prothesis

    fun delete(id: String)

    fun deleteOffline()

    fun setOffline(id: String): Prothesis

    fun setOnline(id: String): Prothesis

    fun isOnline(id: String): Boolean

    fun changeProthesis(prothesis: Prothesis, id: String): Prothesis

    fun updateSettings(id: String, settings: GetSettingsDto): Prothesis

    fun updateGestures(id: String, gestures: GetGesturesDto): Prothesis
}