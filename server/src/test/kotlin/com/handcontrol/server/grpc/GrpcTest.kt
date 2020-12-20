package com.handcontrol.server.grpc

import com.handcontrol.server.entity.Prothesis
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureActionDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.GetSettingsDto
import com.handcontrol.server.protobuf.*
import com.handcontrol.server.repository.CredentialsRepository
import com.handcontrol.server.service.ProthesisService
import io.github.majusko.grpc.jwt.data.GrpcHeader
import io.github.majusko.grpc.jwt.interceptor.AuthClientInterceptor
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import junit.framework.Assert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableAutoConfiguration
class GrpcTest(@Autowired
               val authClientInterceptor: AuthClientInterceptor
) {
    private lateinit var channel: Channel

    private val target = "localhost:6565"
    private lateinit var signedStub: HandleRequestGrpc.HandleRequestBlockingStub
    private lateinit var stub: HandleRequestGrpc.HandleRequestBlockingStub

    @Autowired
    private lateinit var db: CredentialsRepository
    //todo use in memory db

    @Autowired
    lateinit var proDB: ProthesisService

    @Autowired
    private lateinit var lst: List<MobileWriteApi<*>>

    val acc1 = Prothesis("test1", true, GetSettingsDto(), GetGesturesDto())
    val acc2 = Prothesis("test2", true, GetSettingsDto(), GetGesturesDto())
    val acc3 = Prothesis("test3", true, GetSettingsDto(), GetGesturesDto())
    val acc4 = Prothesis("test4", true, GetSettingsDto(), GetGesturesDto())

    @BeforeAll
    fun registerAndInit() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        stub = HandleRequestGrpc.newBlockingStub(channel)

        val registerResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        signedStub = getSignedStub(registerResponse.token)

        proDB.save(acc1)
        proDB.save(acc2)
        proDB.save(acc3)
        proDB.save(acc4)
    }

    @AfterAll
    fun deleteRegister(){
        db.deleteById(db.findByLogin("login")!!.id)
        proDB.delete(acc1.id)
        proDB.delete(acc2.id)
        proDB.delete(acc3.id)
        proDB.delete(acc4.id)
    }

    @Test
    fun loginAuth() {
        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        assertThat(loginResponse.token != null)
    }

    @Test
    fun loginUnAuth() {
        val exception: Exception = assertThrows(RuntimeException::class.java) {
            stub.login(Request.LoginRequest.newBuilder().setLogin("noLogin").setPassword("noPass").build())
        }
        assertTrue(exception.message!!.contains("PERMISSION_DENIED"))
    }

    @Test
    fun loginWrongLogin() {
        val exception: Exception = assertThrows(RuntimeException::class.java) {
            stub.login(Request.LoginRequest.newBuilder().setLogin("noLogin").setPassword("pass").build())
        }
        assertTrue(exception.message!!.contains("PERMISSION_DENIED"))
    }

    @Test
    fun loginWrongPass() {
        val exception: Exception = assertThrows(RuntimeException::class.java) {
            stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("noPass").build())
        }
        assertTrue(exception.message!!.contains("PERMISSION_DENIED"))
    }

    @Test
    fun getOnline() {
        val response = signedStub.getOnline(Request.getOnlineRequest.newBuilder().build())
        assertTrue(response.list.contains("test1"))
        assertTrue(response.list.contains("test2"))
        assertTrue(response.list.contains("test3"))
        assertTrue(response.list.contains("test4"))
    }

    @Test
    fun getSettings() {
        val response = signedStub.getSettings(Request.getSettingsRequest.newBuilder().setId(acc1.id).build())
        assertEquals(acc1.settings?.typeWork, response.settings.typeWork)
        assertEquals(acc1.settings?.enableEmg, response.settings.enableEmg)
        assertEquals(acc1.settings?.enableDisplay, response.settings.enableDisplay)
        assertEquals(acc1.settings?.enableGyro, response.settings.enableGyro)
        assertEquals(acc1.settings?.enableDriver, response.settings.enableDriver)
    }

    @Test
    fun getSettingsNotExist() {
        val exception: Exception = assertThrows(RuntimeException::class.java) {
            val response = signedStub.getSettings(Request.getSettingsRequest.newBuilder().setId("notExistId").build())
        }
        assertTrue(exception.message!!.contains("NOT_FOUND"))
    }

    @Test
    //failed test
    fun setSettings() {
        val acc5 = Prothesis("test5", true, GetSettingsDto(), GetGesturesDto())
        proDB.save(acc5)

        val settings = Settings.SetSettings.newBuilder()
        settings.telemetryFrequency = 100
        settings.enableDisplay = true
        settings.enableDriver = true
        settings.enableEmg = true
        settings.enableGyro = true
        settings.typeWork = Enums.ModeType.MODE_AUTO

        signedStub.setSettings(Request.setSettingsRequest.newBuilder()
                .setId(acc5.id).setSettings(settings).build())
        Thread.sleep(3000)
        val response = signedStub.getSettings(Request.getSettingsRequest.newBuilder().setId(acc5.id).build())

        //println("get setting " + response.settings.enableDisplay + " " + response.settings.enableDriver+ " " + response.settings.enableGyro)

        assertEquals(settings.enableEmg, response.settings.enableEmg)
        assertEquals(settings.enableDisplay, response.settings.enableDisplay)
        assertEquals(settings.enableGyro, response.settings.enableGyro)
        assertEquals(settings.enableDriver, response.settings.enableDriver)
        assertEquals(settings.typeWork, response.settings.typeWork)

        proDB.delete(acc5.id)
    }

    //@Test
    fun getGestures() {

        val unixTime = 1605088323L

        val a1 = GestureActionDto(1, 1, 1, 1, 1, 1)
        val a2 = GestureActionDto(2, 2, 2, 2, 2, 1)
        val g1 = GestureDto(UuidDto("1"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val g2 = GestureDto(UuidDto("2"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val getGestures = GetGesturesDto(unixTime, listGestures = listOf(g1, g2))

        val acc5 = Prothesis("test5", true, GetSettingsDto(), getGestures)
        proDB.save(acc5)

        val response = signedStub.getGestures(Request.getGesturesRequest.newBuilder().setId(acc1.id).build())
        println(response.gestures.getGestures(0))
        assertEquals(acc5.gestures, response.gestures)

        proDB.delete(acc5.id)
    }

    //@Test
    //not done
    fun saveGesture() {
        val uuid = Uuid.UUID.newBuilder()
        uuid.value = "12345"
        val gestureAction = Gestures.GestureAction.newBuilder()
        gestureAction.delay = 1
        gestureAction.thumbFingerPosition = 1
        gestureAction.littleFingerPosition = 1
        gestureAction.ringFingerPosition = 1
        gestureAction.middleFingerPosition = 1
        gestureAction.pointerFingerPosition = 1
        val gesture = Gestures.Gesture.newBuilder()
        gesture.setId(uuid)
        gesture.name = "name"
        gesture.lastTimeSync = 14124
        gesture.iterable = true
        gesture.repetitions = 1
        gesture.addActions(gestureAction)

        val dt = LocalDateTime.of(2020, 12, 6, 22, 54)
        val unixTime = dt.toEpochSecond(ZoneOffset.UTC)

        val response = signedStub.saveGesture(Request.saveGestureRequest.newBuilder()
                .setId("test4").setGesture(gesture).setTimeSync(unixTime).build())
        //assertTrue(response!= null)

        //println(proDB.getProthesisById("test4").get())
        //wait for getGesture
    }


    //@Test
    fun deleteGesture() {
         val uuid = Uuid.UUID.newBuilder()
        uuid.value = "12345"
        val dt = LocalDateTime.of(2020, 12, 6, 22, 54)
        val unixTime = dt.toEpochSecond(ZoneOffset.UTC)


        val response = signedStub.deleteGesture(Request.deleteGestureRequest.newBuilder()
                .setId("1").setGestureId(uuid).setTimeSync(unixTime).build())
        //assertTrue(response!= null)
    }

    //@Test
    fun performGestureId() {
        val uuid = Uuid.UUID.newBuilder()
        uuid.value = "12345"
        val response = signedStub.performGestureId(Request.performGestureIdRequest.newBuilder()
                .setId("1").setGestureId(uuid).build())
        //assertTrue(response!= null)
    }

    //@Test
    fun performGestureRaw() {
        val uuid = Uuid.UUID.newBuilder()
        uuid.value = "12345"
        val gestureAction = Gestures.GestureAction.newBuilder()
        gestureAction.delay = 1
        gestureAction.thumbFingerPosition = 1
        gestureAction.littleFingerPosition = 1
        gestureAction.ringFingerPosition = 1
        gestureAction.middleFingerPosition = 1
        gestureAction.pointerFingerPosition = 1
        val gesture = Gestures.Gesture.newBuilder()
        gesture.setId(uuid)
        gesture.name = "name"
        gesture.lastTimeSync = 14124
        gesture.iterable = true
        gesture.repetitions = 1
        gesture.addActions(gestureAction)

        val response = signedStub.performGestureRaw(Request.performGestureRawRequest.newBuilder()
                .setId("1").setGesture(gesture).build())
        //assertTrue(response!= null)
    }

    //@Test
    fun setPositions() {
       val response = signedStub.setPositions(Request.setPositionsRequest.newBuilder()
                .setId("1").setPointerFingerPosition(1).setMiddleFingerPosition(2).setRingFingerPosition(3)
                .setLittleFingerPosition(4).setThumbFingerPosition(5).build())
        //assertTrue(response!= null)
    }

    private fun getSignedStub(token: String): HandleRequestGrpc.HandleRequestBlockingStub {
        val header = Metadata()
        header.put(GrpcHeader.AUTHORIZATION, token)
        val stub = HandleRequestGrpc.newBlockingStub(channel)
        return MetadataUtils.attachHeaders(stub, header)
    }
    //todo

}