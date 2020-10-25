package com.handcontrol.server

import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.repository.CredentialsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
//todo replace with in-memmory db and implement tests
//if it is really needed to test this
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaTest(@Autowired val credentialsRepository: CredentialsRepository,
              @Autowired val entityManager: TestEntityManager) {

    @Test
    fun add() {
        val a = Credentials("login", "password")
        entityManager.persist(a)
        entityManager.flush()
        credentialsRepository.save(a)
        val b = credentialsRepository.findByLogin("login")
        assertThat(a).isEqualTo(b)
    }
}