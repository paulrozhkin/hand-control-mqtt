package com.handcontrol.server.repository
import com.handcontrol.server.entity.Credentials
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface CredentialsRepository : CrudRepository<Credentials, Long> {

    fun findByLogin(login: String): Credentials?
}