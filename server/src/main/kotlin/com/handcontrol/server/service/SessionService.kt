package com.handcontrol.server.service

import com.handcontrol.server.entity.Session
import org.springframework.stereotype.Service

@Service
interface SessionService {

    fun getSessionByLogin(login: String): Session

    fun getAllSessions(): List<Session>

    fun save(session: Session): Session

    fun delete(login: String)
}