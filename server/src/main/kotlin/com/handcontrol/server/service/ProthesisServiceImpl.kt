package com.handcontrol.server.service

import com.handcontrol.server.entity.Prothesis
import com.handcontrol.server.service.dao.ProthesisRepository
import org.springframework.stereotype.Service

@Service
class ProthesisServiceImpl(val repository: ProthesisRepository) : ProthesisService {
    override fun getProthesisById(id: String): Prothesis {
        return repository.findById(id).get()
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
}