package com.handcontrol.server.dao

import com.handcontrol.server.entity.Session
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository: CrudRepository<Session, String>