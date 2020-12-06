package com.handcontrol.server.service

import com.handcontrol.server.entity.Settings
import com.handcontrol.server.service.dao.SettingsRepository
import org.springframework.stereotype.Service

//@Service
class SettingsServiceImpl(val repository: SettingsRepository): SettingsService {

    override fun getSettingsById(id: String): Settings {
        return repository.findById(id).get()
    }

    override fun getAllSettings(): List<Settings> {
        return repository.findAll().toList()
    }

    override fun save(settings: Settings): Settings {
        return repository.save(settings)
    }

    override fun delete(id: String) {
        return repository.deleteById(id)
    }
}