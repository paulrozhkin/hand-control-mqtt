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

    override fun setOffline(id: String): Prothesis {
        val prothesis: Prothesis = repository.findById(id).get()
        prothesis.isOnline = false
        return repository.save(prothesis)
    }

    override fun isOnline(id: String): Boolean {
        return repository.findById(id).get().isOnline
    }

    override fun changeProthesis(prothesis: Prothesis, id: String): Prothesis {
        val prothesisNew: Prothesis = repository.findById(id).get()
        repository.save(prothesis)
        return prothesisNew
    }

}