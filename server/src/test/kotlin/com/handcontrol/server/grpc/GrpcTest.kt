package com.handcontrol.server.grpc

import com.handcontrol.server.protobuf.*
import com.handcontrol.server.repository.CredentialsRepository
import io.github.majusko.grpc.jwt.data.GrpcHeader
import io.github.majusko.grpc.jwt.interceptor.AuthClientInterceptor
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Autowired
    lateinit var db: CredentialsRepository
    //todo use in memory db


    @BeforeAll
    fun register() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val registerResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        assertThat(registerResponse.token != null)
    }

    @AfterAll
    fun deleteRegister(){
        db.deleteById(db.findByLogin("login")!!.id)
    }

    @Test
    fun login() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        assertThat(loginResponse.token != null)
    }

    @Test
    fun loginUnAuth() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val exception: Exception = assertThrows(RuntimeException::class.java) {
            stub.login(Request.LoginRequest.newBuilder().setLogin("noLogin").setPassword("noPass").build())
        }
        assertTrue(exception.message!!.contains("PERMISSION_DENIED"))
    }

    @Test
    fun getOnline() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val response = signedStub.getOnline(Request.getOnlineRequest.newBuilder().build())
        assertTrue(response!= null)
    }

    @Test
    fun setOffline() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val response = signedStub.setOffline(Request.setOfflineRequest.newBuilder().setId("1").build())
        assertTrue(response!= null)
    }

    @Test
    fun getSettings() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val response = signedStub.getSettings(Request.getSettingsRequest.newBuilder().setId("1").build())
        //println(response.settings)
        assertTrue(response.settings!= null)
    }

    @Test
    fun setSettings() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val settings = Settings.SetSettings.newBuilder()
        settings.telemetryFrequency = 100
        settings.enableDisplay = true
        settings.enableDriver = true
        settings.enableEmg = true
        settings.enableGyro = true
        settings.typeWork = Enums.ModeType.MODE_AUTO

        val response = signedStub.setSettings(Request.setSettingsRequest.newBuilder()
                .setId("1").setSettings(settings).build())
        //println(response.settings)
        assertTrue(response.settings!= null)
    }

    @Test
    fun getGestures() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val response = signedStub.getGestures(Request.getGesturesRequest.newBuilder().setId("1").build())
        //println(response.settings)
        assertTrue(response.gestures!= null)
    }

    @Test
    fun saveGesture() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

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
                .setId("1").setGesture(gesture).setTimeSync(unixTime).build())
        assertTrue(response!= null)
    }


    @Test
    fun deleteGesture() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)
        val uuid = Uuid.UUID.newBuilder()
        uuid.value = "12345"
        val dt = LocalDateTime.of(2020, 12, 6, 22, 54)
        val unixTime = dt.toEpochSecond(ZoneOffset.UTC)


        val response = signedStub.deleteGesture(Request.deleteGestureRequest.newBuilder()
                .setId("1").setGestureId(uuid).setTimeSync(unixTime).build())
        assertTrue(response!= null)
    }

    @Test
    fun performGestureId() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val uuid = Uuid.UUID.newBuilder()
        uuid.value = "12345"
        val response = signedStub.performGestureId(Request.performGestureIdRequest.newBuilder()
                .setId("1").setGestureId(uuid).build())
        assertTrue(response!= null)
    }

    @Test
    fun performGestureRaw() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

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
        assertTrue(response!= null)
    }

    @Test
    fun setPositions() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(loginResponse.token)

        val response = signedStub.setPositions(Request.setPositionsRequest.newBuilder()
                .setId("1").setPointerFingerPosition(1).setMiddleFingerPosition(2).setRingFingerPosition(3)
                .setLittleFingerPosition(4).setThumbFingerPosition(5).build())
        assertTrue(response!= null)
    }


    private fun getSignedStub(token: String): HandleRequestGrpc.HandleRequestBlockingStub {
        val header = Metadata()

        header.put(GrpcHeader.AUTHORIZATION, token)

        val stub = HandleRequestGrpc.newBlockingStub(channel)

        return MetadataUtils.attachHeaders(stub, header)
    }

    //todo

}