package com.handcontrol.server.service

import com.handcontrol.server.entity.Session
import com.handcontrol.server.service.dao.SessionRepository
import org.springframework.stereotype.Service

@Service
class SessionServiceImpl(val repository: SessionRepository) : SessionService {

    override fun getSessionByLogin(login: String): Session = repository.findById(login).get()

    override fun getAllSessions(): List<Session> = repository.findAll().toList()

    override fun save(session: Session): Session =  repository.save(session)

    override fun delete(login: String) = repository.deleteById(login)
}