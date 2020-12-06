package com.handcontrol.server.service.dao

import com.handcontrol.server.entity.Settings
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SettingsRepository : CrudRepository<Settings, String>
