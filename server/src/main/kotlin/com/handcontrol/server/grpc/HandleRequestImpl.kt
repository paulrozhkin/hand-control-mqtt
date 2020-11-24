package com.handcontrol.server.grpc

import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.set.*
import com.handcontrol.server.protobuf.*
import com.handcontrol.server.repository.CredentialsRepository
import io.github.majusko.grpc.jwt.annotation.Allow
import io.github.majusko.grpc.jwt.service.JwtService
import io.github.majusko.grpc.jwt.service.dto.JwtData
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@GRpcService
class HandleRequestImpl(private val jwtService: JwtService, private val db: CredentialsRepository) : HandleRequestGrpc.HandleRequestImplBase() {

    private val logger = LoggerFactory.getLogger(HandleRequestImpl::class.java)

    @Autowired
    private lateinit var lst: List<MobileWriteApi<*>>

    companion object {
        const val USER = "user"
    }

    //todo add everywhere checks for token - prosthesis
    //todo change when to validate() method

    override fun login(request: Request.LoginRequest, responseObserver: StreamObserver<Request.LoginResponse>) {
        try {
            val user = db.findByLogin(request.login)
            if (user == null) {
                logger.error("No user in db found with login {}", request.login)
                responseObserver.onError(Status.PERMISSION_DENIED.asRuntimeException())
            } else {
                val pas = user.hash
                //todo hash
                if (request.password != pas) {
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
    override fun getOnline(request: Request.getOnlineRequest?, responseObserver: StreamObserver<Request.getOnlineResponse>?) {
        //todo get online prothesis from redis
        val message = Request.getOnlineResponse.newBuilder().setList(listOf("981283", "1233").toString()).build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun setOffline(request: Request.setOfflineRequest?, responseObserver: StreamObserver<Request.setOfflineResponse>?) {
        //todo redis
        val id = request?.id
        when {
            id == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
                //unsuccessful
                //todo no such id
            }
            id == "error" -> {
                responseObserver?.onError(Status.CANCELLED.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        val message = Request.setOfflineResponse.newBuilder().build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun getSettings(request: Request.getSettingsRequest?, responseObserver: StreamObserver<Request.getSettingsResponse>?) {
        val id = request?.id
        //todo find settings from redis, handle if no
        when {
            id == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        val settings = Settings.GetSettings.newBuilder()
        settings.enableDisplay = true
        settings.enableDriver = true
        settings.enableEmg = true
        settings.enableGyro = true
        settings.typeWork = Enums.ModeType.MODE_AUTO
        val message = Request.getSettingsResponse.newBuilder().setSettings(settings.build()).build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun setSettings(request: Request.setSettingsRequest?, responseObserver: StreamObserver<Request.setSettingsResponse>?) {
        val id = request?.id
        val s = request?.settings
        when {
            id == null || s == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        //todo add mqtt and redis
        val command = lst.find { it is SetSettings } as MobileWriteApi<Settings.SetSettings>
        command.writeToProsthesis(id!!, s!!)
        //todo do we need getSettings?
        val settings = Settings.GetSettings.newBuilder()
        settings.enableDisplay = true
        settings.enableDriver = true
        settings.enableEmg = true
        settings.enableGyro = true
        settings.typeWork = Enums.ModeType.MODE_AUTO
        val message = Request.setSettingsResponse.newBuilder().setSettings(settings).build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()

    }

    @Allow(roles = [USER])
    override fun getGestures(request: Request.getGesturesRequest?, responseObserver: StreamObserver<Request.getGesturesResponse>?) {
        val id = request?.id
        when {
            id == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        //todo add redis
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
        val gestures = Gestures.GetGestures.newBuilder()
        gestures.addGestures(gesture)
        val message = Request.getGesturesResponse.newBuilder().setGestures(gestures.build()).build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun saveGesture(request: Request.saveGestureRequest?, responseObserver: StreamObserver<Request.saveGestureResponse>?) {
        val gesture = request?.gesture
        val id = request?.id
        val time = request?.timeSync
        when {
            id == null || gesture == null || time == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        val saveGesture = Gestures.SaveGesture.newBuilder()
        saveGesture.timeSync = time!!
        saveGesture.gesture = gesture
        val command = lst.find { it is SaveGesture } as MobileWriteApi<Gestures.SaveGesture>
        command.writeToProsthesis(id!!, saveGesture.build())
        //todo add mqtt
        //todo telemetry
        val message = Request.saveGestureResponse.newBuilder().build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun deleteGesture(request: Request.deleteGestureRequest?, responseObserver: StreamObserver<Request.deleteGestureResponse>?) {
        val id = request?.id
        val gestureId = request?.gestureId
        val time = request?.timeSync
        when {
            id == null || gestureId == null || time == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
            //todo change
            gestureId == Uuid.UUID.newBuilder().setValue("error").build() -> {
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
        }
        val deleteGesture = Gestures.DeleteGesture.newBuilder()
        deleteGesture.timeSync = time!!
        deleteGesture.id = gestureId
        val command = lst.find { it is DeleteGesture } as MobileWriteApi<Gestures.DeleteGesture>
        command.writeToProsthesis(id!!, deleteGesture.build())

        //todo add mqtt
        //todo telemetry
        val message = Request.deleteGestureResponse.newBuilder().build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun performGestureId(request: Request.performGestureIdRequest?, responseObserver: StreamObserver<Request.performGestureIdResponse>?) {
        val id = request?.id
        val gestureId = request?.gestureId
        when {
            id == null || gestureId == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
            //todo change
            gestureId == Uuid.UUID.newBuilder().setValue("error").build() -> {
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
        }
        val performGestureById = Gestures.PerformGestureById.newBuilder()
        performGestureById.id = gestureId
        val command = lst.find { it is PerformGestureById } as MobileWriteApi<Gestures.PerformGestureById>
        command.writeToProsthesis(id!!, performGestureById.build())

        //todo add mqtt
        val message = Request.performGestureIdResponse.newBuilder().build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun performGestureRaw(request: Request.performGestureRawRequest?, responseObserver: StreamObserver<Request.performGestureRawResponse>?) {
        val id = request?.id
        val gesture = request?.gesture
        when {
            id == null || gesture == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        val performGestureRaw = Gestures.PerformGestureRaw.newBuilder()
        performGestureRaw.gesture = gesture
        val command = lst.find { it is PerformGestureRaw } as MobileWriteApi<Gestures.PerformGestureRaw>
        command.writeToProsthesis(id!!, performGestureRaw.build())
        //todo add mqtt
        val message = Request.performGestureRawResponse.newBuilder().build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    @Allow(roles = [USER])
    override fun setPositions(request: Request.setPositionsRequest?, responseObserver: StreamObserver<Request.setPositionsResponse>?) {
        val id = request?.id
        val pointerPosition = request?.pointerFingerPosition
        val middlePosition = request?.middleFingerPosition
        val ringPosition = request?.ringFingerPosition
        val littlePosition = request?.littleFingerPosition
        val thumbPosition = request?.thumbFingerPosition
        when {
            id == null || pointerPosition == null
                    || middlePosition == null
                    || ringPosition == null
                    || littlePosition == null
                    || thumbPosition == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            id == "error" -> {
                //todo no such id
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        //todo handle mqtt
        val setPositions = Gestures.SetPositions.newBuilder()
        setPositions.pointerFingerPosition = pointerPosition!!
        setPositions.middleFingerPosition = middlePosition!!
        setPositions.ringFingerPosition = ringPosition!!
        setPositions.littleFingerPosition = littlePosition!!
        setPositions.thumbFingerPosition = thumbPosition!!
        val command = lst.find { it is SetPositions } as MobileWriteApi<Gestures.SetPositions>
        command.writeToProsthesis(id!!, setPositions.build())
        val message = Request.setPositionsResponse.newBuilder().build()
        responseObserver?.onNext(message)
        responseObserver?.onCompleted()
    }

    private fun checkRights(id: String): Boolean {
        //todo implement
        return id == "norights"
    }
}
