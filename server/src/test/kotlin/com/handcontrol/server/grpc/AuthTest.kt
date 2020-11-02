package com.handcontrol.server.grpc

import com.handcontrol.server.HandleRequestGrpc
import com.handcontrol.server.Request

import io.github.majusko.grpc.jwt.interceptor.AuthClientInterceptor
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import org.assertj.core.api.Assertions.assertThat
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
class AuthTest(@Autowired
                val authClientInterceptor: AuthClientInterceptor
) {
    private lateinit var channel: Channel

    private val target = "localhost:6565"

    //todo use in memory db

    @Test
    fun testRequest() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        val stub = HandleRequestGrpc.newBlockingStub(channel)
        val loginResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").build())
        assertThat(loginResponse.token != null)
        val response = stub.proRequest(Request.ClientRequest.newBuilder().setRequest("request").build())
        assert(response.message != null)
    }

    //todo

}