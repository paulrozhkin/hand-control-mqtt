package com.handcontrol.server.service

import com.handcontrol.server.entity.Settings
import org.springframework.stereotype.Service

@Service
interface SettingsService {

    fun getSettingsById(id: String): Settings

    fun getAllSettings(): List<Settings>

    fun save(settings: Settings): Settings

    fun delete(id: String)
}