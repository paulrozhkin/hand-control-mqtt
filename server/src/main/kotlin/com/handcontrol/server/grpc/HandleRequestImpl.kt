package com.handcontrol.server.grpc

import com.handcontrol.server.HandleRequestGrpc
import com.handcontrol.server.Request
import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.repository.CredentialsRepository
import io.github.majusko.grpc.jwt.annotation.Allow
import io.github.majusko.grpc.jwt.data.GrpcJwtContext
import io.github.majusko.grpc.jwt.service.GrpcRole
import io.github.majusko.grpc.jwt.service.JwtService
import io.github.majusko.grpc.jwt.service.dto.JwtData
import org.lognet.springboot.grpc.GRpcService
import io.grpc.stub.StreamObserver
import org.springframework.beans.factory.annotation.Autowired

@GRpcService
class HandleRequestImpl(private val jwtService: JwtService, @Autowired val db: CredentialsRepository) : HandleRequestGrpc.HandleRequestImplBase() {

    companion object {
        const val USER = "user"
    }

    @Allow(roles = [GrpcRole.INTERNAL])
    override fun login(request: Request.LoginRequest, responseObserver: StreamObserver<Request.LoginResponse>) {
        val user = db.findByLogin(request.login)
        val token: String
        if (user != null) {
            val jwtData = JwtData(request.imei, setOf(USER))
            token = jwtService.generate(jwtData)
        } else {
            //todo change
            token = "error"
        }
        //todo add redis
        val proto = Request.LoginResponse.newBuilder()
                .setToken(token)
                .build()
        responseObserver.onNext(proto)
        responseObserver.onCompleted()
    }

    @Allow(roles = [GrpcRole.INTERNAL])
    override fun registry(request: Request.LoginRequest, responseObserver: StreamObserver<Request.LoginResponse>) {
        val log = request.login
        val pas = request.password
        val credentials = Credentials(log, pas)
        db.save(credentials)
        login(request, responseObserver)
    }

    @Allow(roles = [USER])
    override fun proRequest(request: Request.ClientRequest, responseObserver: StreamObserver<Request.ClientResponse>) {
        //todo send unauthorized instead of throwing exception
        val auth = GrpcJwtContext.get().orElseThrow { throw Exception("Missing auth data!") }
        //todo handle request
        val message = Request.ClientResponse.newBuilder()
                .setMessage("id: " + auth.userId + ". jwt: " + auth.jwt)
                .build()
        responseObserver.onNext(message)
        responseObserver.onCompleted()
    }
}
