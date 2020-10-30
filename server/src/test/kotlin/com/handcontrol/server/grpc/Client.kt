package com.handcontrol.server.grpc

import com.google.protobuf.Empty
import com.handcontrol.server.Application
import com.handcontrol.server.HandleRequestGrpc
import com.handcontrol.server.Request

import io.github.majusko.grpc.jwt.data.GrpcHeader
import io.github.majusko.grpc.jwt.interceptor.AuthClientInterceptor
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import javax.annotation.PostConstruct

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableAutoConfiguration
class Client(   @Autowired
                val authClientInterceptor: AuthClientInterceptor
) {



    private lateinit var channel: Channel

    private val target = "localhost:6565"

    @Test
    fun loginAndUpdateName() {
        val c = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        channel = ClientInterceptors.intercept(c, authClientInterceptor)
        //login - validated by default internal token
        val stub = HandleRequestGrpc.newBlockingStub(channel)
        val loginResponse = stub.registry(Request.LoginRequest.newBuilder().setLogin("login").setPassword("pass").setImei("12414").build())
    }

}