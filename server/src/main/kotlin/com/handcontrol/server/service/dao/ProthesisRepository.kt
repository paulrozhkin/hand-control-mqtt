package com.handcontrol.server.service.dao

import com.handcontrol.server.entity.Prothesis
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProthesisRepository: CrudRepository<Prothesis, String> {

    fun findAllByOnline(isOnline: Boolean): List<Prothesis>

    fun deleteAllByOnline(isOnline: Boolean)
}