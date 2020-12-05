package com.handcontrol.server.service

import com.handcontrol.server.entity.Prothesis
import org.springframework.stereotype.Service

@Service
interface ProthesisService {

    fun getProthesisById(id: String): Prothesis

    fun gelAllOnlineProtheses(): List<Prothesis>

    fun save(prothesis: Prothesis): Prothesis

    fun delete(id: String)

    fun deleteOffline()

    fun setOffline(id: String): Prothesis

    fun isOnline(id: String): Boolean

    fun changeProthesis(prothesis: Prothesis, id: String): Prothesis
}