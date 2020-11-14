package com.handcontrol.server.grpc

import com.handcontrol.server.protobuf.HandleRequestGrpc
import com.handcontrol.server.protobuf.Request
import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.repository.CredentialsRepository
import io.github.majusko.grpc.jwt.annotation.Allow
import io.github.majusko.grpc.jwt.data.GrpcJwtContext
import io.github.majusko.grpc.jwt.service.JwtService
import io.github.majusko.grpc.jwt.service.dto.JwtData
import io.grpc.Status
import org.lognet.springboot.grpc.GRpcService
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@GRpcService
class HandleRequestImpl(private val jwtService: JwtService, @Autowired val db: CredentialsRepository) : HandleRequestGrpc.HandleRequestImplBase() {

    private val logger = LoggerFactory.getLogger(HandleRequestImpl::class.java)

    companion object {
        const val USER = "user"
    }
    
    override fun login(request: Request.LoginRequest, responseObserver: StreamObserver<Request.LoginResponse>) {
        try {
            val user = db.findByLogin(request.login)
            if (user == null) {
                logger.error("No user in db found with login {}", request.login)
                responseObserver.onError(Status.PERMISSION_DENIED.asRuntimeException())
            } else {
                val pas = user.hash
                //todo hash
                if (request.password != pas){
                    logger.error("User {} entered incorrect password", request.login)
                    responseObserver.onError(Status.PERMISSION_DENIED.asRuntimeException())
                }
            }
        } catch (e: Exception) {
            logger.error("Caught error while trying to find user ub db. {}", e.message)
            responseObserver.onError(Status.PERMISSION_DENIED.asRuntimeException())
        }
        val token: String
        val jwtData = JwtData(request.login, setOf(USER))
        token = jwtService.generate(jwtData)

        //todo add redis
        val proto = Request.LoginResponse.newBuilder()
                .setToken(token)
                .build()
        responseObserver.onNext(proto)
        responseObserver.onCompleted()
    }
    
    override fun registry(request: Request.LoginRequest, responseObserver: StreamObserver<Request.LoginResponse>) {
        val log = request.login
        val pas = request.password
        //todo hash
        logger.info("Trying to register new user {}", log)
        val user: Credentials?
        try {
            user = db.findByLogin(request.login)
            if (user != null) {
                responseObserver.onError(Status.ALREADY_EXISTS.asRuntimeException())
                return
            }
        } catch (e: Exception) {
            logger.error("Caught error while trying to check if user already exists. {}", e.message)
        }
        val credentials = Credentials(log, pas)
        try {
            db.save(credentials)
            logger.info("User {} saved to db", log)
        } catch (e: Exception) {
            logger.error("Caught error while trying to save user to db. {}", e.message)
            responseObserver.onError(Status.UNKNOWN.asRuntimeException())
            return
        }
        val jwtData = JwtData(log, setOf(USER))
        val token = jwtService.generate(jwtData)
        //todo add redis
        val proto = Request.LoginResponse.newBuilder()
                .setToken(token)
                .build()
        responseObserver.onNext(proto)
        responseObserver.onCompleted()
        logger.info("User {} successfully registered", log)
    }

    @Allow(roles = [USER])
    override fun proRequest(request: Request.ClientRequest, responseObserver: StreamObserver<Request.ClientResponse>) {
        val optional = GrpcJwtContext.get()
        if (!optional.isPresent) {
            logger.error("Can't get token")
            responseObserver.onError(Status.UNAUTHENTICATED.asRuntimeException())
            return
        }
        val auth = optional.get()
        //todo handle request
        val message = Request.ClientResponse.newBuilder()
                .setMessage("id: " + auth.userId + ". jwt: " + auth.jwt)
                .build()
        responseObserver.onNext(message)
        responseObserver.onCompleted()
    }
}
