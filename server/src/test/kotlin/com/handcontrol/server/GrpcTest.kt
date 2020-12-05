package com.handcontrol.server.grpc

import com.handcontrol.server.protobuf.HandleRequestGrpc
import com.handcontrol.server.protobuf.Request
import com.handcontrol.server.repository.CredentialsRepository
import io.github.majusko.grpc.jwt.data.GrpcHeader
import io.github.majusko.grpc.jwt.interceptor.AuthClientInterceptor
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


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


    @Test
    fun register() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val registerResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        assertThat(registerResponse.token != null)

        db.deleteById(db.findByLogin("login")!!.id)
    }


    @Test
    fun loginAuth() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val loginResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        assertThat(loginResponse.token != null)
        db.deleteById(db.findByLogin("login")!!.id)
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
    fun requestAuth() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        val registerResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(registerResponse.token)

        val response = signedStub.proRequest(Request.ClientRequest.newBuilder().setRequest("hello").build())
        println(response.message)
        db.deleteById(db.findByLogin("login")!!.id)
    }

    @Test
    fun requestUnAuth() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)

        val signedStub = getSignedStub("noToken")

        val exception: Exception = assertThrows(RuntimeException::class.java) {
            signedStub.proRequest(Request.ClientRequest.newBuilder().setRequest("hello").build())
        }

        assertTrue(exception.message!!.contains("UNAUTHENTICATED"))
    }

    // Test not finish: stream work, but need to logout to stop stream, now it is infinity loop
    //@Test
    fun stream() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)

        //register for 1st time run
        val registerResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        //for 2nd time and so on
        //val registerResponse = stub.login(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())

        val signedStub = getSignedStub(registerResponse.token)

        val response = signedStub.proUpdate(Request.SubscribeRequest.newBuilder().setMessage("subscribe").build())

        while (true) {
            response.forEachRemaining {
                println("response " + it)
                Thread.sleep(2000)
            }
        }

        db.deleteById(db.findByLogin("login")!!.id)
    }

    private fun getSignedStub(token: String): HandleRequestGrpc.HandleRequestBlockingStub {
        val header = Metadata()

        header.put(GrpcHeader.AUTHORIZATION, token)

        val stub = HandleRequestGrpc.newBlockingStub(channel)

        return MetadataUtils.attachHeaders(stub, header)
    }

    //todo

}