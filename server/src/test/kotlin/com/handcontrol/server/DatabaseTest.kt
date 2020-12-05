package com.handcontrol.server.grpc

import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.repository.CredentialsRepository
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest
class DatabaseTest {

    @Autowired
    lateinit var db: CredentialsRepository

    val acc1 = Credentials("acc1", "pass1")
    val acc2 = Credentials("acc2", "pass2")

    @Test
    fun testCredential(){
        val before = db.count()

        //create
        db.save(acc1)
        db.save(acc2)
        assertEquals(before + 2, db.count())

        //check data
        val queryResult = db.findByLogin(acc1.login)
        assertNotNull(queryResult);

        assertEquals(acc1.login, queryResult!!.login);
        assertEquals(acc1.hash, queryResult!!.hash);

        //delete
        db.deleteById(db.findByLogin(acc1.login)!!.id)
        db.deleteById(db.findByLogin(acc2.login)!!.id)
        assertEquals(before, db.count())
    }
}