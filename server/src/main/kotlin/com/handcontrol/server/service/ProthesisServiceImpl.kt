package com.handcontrol.server.service

import com.handcontrol.server.entity.Prothesis
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import com.handcontrol.server.service.dao.ProthesisRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProthesisServiceImpl(val repository: ProthesisRepository) : ProthesisService {

    override fun getProthesisById(id: String): Optional<Prothesis> {
        return repository.findById(id)
    }

    override fun gelAllOnlineProtheses(): List<Prothesis> {
        return repository.findAllByOnline(true)
    }

    override fun save(prothesis: Prothesis): Prothesis {
        return repository.save(prothesis)
    }

    override fun delete(id: String) {
        repository.deleteById(id)
    }

    override fun deleteOffline() {
        repository.deleteAllByOnline(false)
    }

    override fun setOffline(id: String): Prothesis {
        val prosthesis: Prothesis = repository.findById(id).orElseGet { Prothesis.createWith(id) }
        prosthesis.isOnline = false
        return repository.save(prosthesis)
    }

    override fun setOnline(id: String): Prothesis {
        val prosthesis: Prothesis = repository.findById(id).orElseGet { Prothesis.createWith(id) }
        prosthesis.isOnline = true
        return repository.save(prosthesis)
    }

    override fun isOnline(id: String): Boolean {
        val findOpt = repository.findById(id)
        return findOpt.map { it.isOnline }.orElse(false)
    }

    override fun changeProthesis(prothesis: Prothesis, id: String): Prothesis {
        val prothesisNew: Prothesis = repository.findById(id).get()
        repository.save(prothesis)
        return prothesisNew
    }

    override fun updateSettings(id: String, settings: GetSettingsDto): Prothesis {
        val prosthesis: Prothesis = repository.findById(id).orElseGet { Prothesis.createWith(id) }
        prosthesis.isOnline = true
        prosthesis.settings = settings
        return repository.save(prosthesis)
    }

    override fun updateGestures(id: String, gestures: GetGesturesDto): Prothesis {
        val prosthesis: Prothesis = repository.findById(id).orElseGet { Prothesis.createWith(id) }
        prosthesis.isOnline = true
        prosthesis.gestures = gestures
        return repository.save(prosthesis)
    }

}