package com.handcontrol.server.dao

import com.handcontrol.server.entity.Session
import org.springframework.stereotype.Service

@Service
class SessionServiceImpl(val repository: SessionRepository) : SessionService {
    override fun getSession(id: String): Session = repository.findById(id).get()

    override fun getAllSessions(): List<Session> = repository.findAll().toList()

    override fun createSession(session: Session): Session =  repository.save(session)

    override fun deleteSession(id: String) = repository.deleteById(id)
}