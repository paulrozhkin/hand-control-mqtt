package com.handcontrol.server.grpc

import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.entity.Prothesis
import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureActionDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import com.handcontrol.server.protobuf.Enums
import com.handcontrol.server.protobuf.HandleRequestGrpc
import com.handcontrol.server.protobuf.Request
import com.handcontrol.server.repository.CredentialsRepository
import com.handcontrol.server.service.ProthesisService
import com.handcontrol.server.service.dao.ProthesisRepository
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import junit.framework.Assert.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class DatabaseTest {

    @Autowired
    lateinit var accountDB: CredentialsRepository

    @Autowired
    lateinit var proDB: ProthesisService

    val acc1 = Prothesis("test1", true, GetSettingsDto(), GetGesturesDto())
    val acc2 = Prothesis("test2", true, GetSettingsDto(), GetGesturesDto())
    val acc3 = Prothesis("test3", false, GetSettingsDto(), GetGesturesDto())
    val acc4 = Prothesis("test4", false, GetSettingsDto(), GetGesturesDto())

    @Test
    fun testCredential(){
        val acc1 = Credentials("acc1", "pass1")
        val acc2 = Credentials("acc2", "pass2")

        val before = accountDB.count()

        //create
        accountDB.save(acc1)
        accountDB.save(acc2)
        assertEquals(before + 2, accountDB.count())

        //check data
        val queryResult = accountDB.findByLogin(acc1.login)
        assertNotNull(queryResult);

        assertEquals(acc1.login, queryResult!!.login);
        assertEquals(acc1.hash, queryResult!!.hash);

        //delete
        accountDB.deleteById(accountDB.findByLogin(acc1.login)!!.id)
        accountDB.deleteById(accountDB.findByLogin(acc2.login)!!.id)
        assertEquals(before, accountDB.count())
    }

    @BeforeAll
    fun initDB(){
        proDB.save(acc1)
        proDB.save(acc2)
        proDB.save(acc3)
        proDB.save(acc4)
    }

    @AfterAll
    fun deleteInit(){
        proDB.delete(acc1.id)
        proDB.delete(acc2.id)
        proDB.delete(acc3.id)
        proDB.delete(acc4.id)
    }


    @Test
    fun getProthesisById(){
        assertEquals(acc1.id, proDB.getProthesisById(acc1.id).get().id)
        assertEquals(acc2.id, proDB.getProthesisById(acc2.id).get().id)
    }

    @Test
    fun getAllOnlineProtheses(){
        //println(proDB.getProthesisById(acc1.id).get())
        //println(proDB.getProthesisById(acc2.id).get())

        var listRes = proDB.getAllOnlineProtheses()
        //println(listRes.get(0))
        assertEquals(listRes.get(0), acc1)
        assertEquals(listRes.get(1), acc2)
    }


    @Test
    fun setOffline(){
        proDB.setOffline(acc1.id)
        val queryResult = proDB.getProthesisById(acc1.id).get()
        assertFalse(queryResult.isOnline)
        proDB.setOnline(acc1.id)
    }

    @Test
    fun setOnline(){
        proDB.setOffline(acc1.id)

        //setOnline
        proDB.setOnline(acc1.id)
        val queryResult = proDB.getProthesisById(acc1.id).get()
        assertTrue(queryResult.isOnline)

    }

    @Test
    fun isOnline(){
        var queryResult = proDB.getProthesisById(acc2.id).get()
        println(queryResult)
        assertTrue(proDB.isOnline(acc2.id))
    }

    @Test
    fun changeProthesis(){
        val acc5 = Prothesis("test5", false, GetSettingsDto(), GetGesturesDto())
        proDB.save(acc5)
        //println(proDB.getProthesisById(acc5.id).get())
        val tmp = Prothesis(acc5.id, true,
                GetSettingsDto(Enums.ModeType.MODE_AUTO, true, true, true, true),
                GetGesturesDto())
        proDB.changeProthesis(tmp, acc5.id)
        val query = proDB.getProthesisById(acc5.id).get()
        //println(query)
        assertEquals(tmp, query)
        proDB.delete(acc5.id)
    }

    @Test
    fun updateSettings(){
        val acc5 = Prothesis("test5", false, GetSettingsDto(), GetGesturesDto())
        proDB.save(acc5)
        //println(proDB.getProthesisById(acc5.id).get())
        val tmp =  GetSettingsDto(Enums.ModeType.MODE_AUTO, true, true, true, true)
        proDB.updateSettings(acc5.id, tmp)
        val query = proDB.getProthesisById(acc5.id).get()
        //println(query)
        assertEquals(tmp, query.settings)
        assertTrue(query.isOnline)
        proDB.delete(acc5.id)
    }

    @Test
    fun updateGestures(){
        val acc5 = Prothesis("test5", false, GetSettingsDto(), GetGesturesDto())
        proDB.save(acc5)
        //println(proDB.getProthesisById(acc5.id).get())
        val unixTime = 1605088323L

        val a1 = GestureActionDto(1, 1, 1, 1, 1, 1)
        val a2 = GestureActionDto(2, 2, 2, 2, 2, 1)
        val g1 = GestureDto(UuidDto("1"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val g2 = GestureDto(UuidDto("2"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val getGestures = GetGesturesDto(unixTime, listGestures = listOf(g1, g2))
        proDB.updateGestures(acc5.id, getGestures)
        val query = proDB.getProthesisById(acc5.id).get()
        //println(query)
        assertEquals(getGestures, query.gestures)
        assertTrue(query.isOnline)
        proDB.delete(acc5.id)
    }

}