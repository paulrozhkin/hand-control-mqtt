package com.handcontrol.server.dao

import com.handcontrol.server.entity.Session

interface SessionService {
    fun getSession(id: String): Session

    fun getAllSessions(): List<Session>

    fun createSession(session: Session): Session

    fun deleteSession(id: String)
}