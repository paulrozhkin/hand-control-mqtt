package com.handcontrol.server.grpc

import com.handcontrol.server.entity.Credentials
import com.handcontrol.server.entity.Session
import com.handcontrol.server.mqtt.command.MobileWriteApi
import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import com.handcontrol.server.mqtt.command.set.*
import com.handcontrol.server.protobuf.Gestures
import com.handcontrol.server.protobuf.HandleRequestGrpc
import com.handcontrol.server.protobuf.Request
import com.handcontrol.server.protobuf.Settings
import com.handcontrol.server.repository.CredentialsRepository
import com.handcontrol.server.service.ProthesisService
import com.handcontrol.server.service.SessionService
import io.github.majusko.grpc.jwt.annotation.Allow
import io.github.majusko.grpc.jwt.service.JwtService
import io.github.majusko.grpc.jwt.service.dto.JwtData
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@GRpcService
class HandleRequestImpl(private val jwtService: JwtService, private val db: CredentialsRepository) :
    HandleRequestGrpc.HandleRequestImplBase() {

    private val logger = LoggerFactory.getLogger(HandleRequestImpl::class.java)

    @Autowired
    private lateinit var lst: List<MobileWriteApi<*>>

    @Autowired
    private lateinit var sessionService: SessionService

    @Autowired
    private lateinit var prothesisService: ProthesisService

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

        kotlin.runCatching {
            val session = Session(login = request.login, null)
            sessionService.save(session)
            logger.info(" Session saved to redis for login ${request.login}")
        }.onFailure {
            logger.error(" Error while trying to save session to redis for ${request.login} . Error: $it ")
        }

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
        kotlin.runCatching {
            val session = Session(login = request.login, null)
            sessionService.save(session)
            logger.info(" Session saved to redis for login ${request.login}")
        }.onFailure {
            logger.error(" Error while trying to save session to redis for ${request.login} . Error: $it")
        }
        val proto = Request.LoginResponse.newBuilder()
            .setToken(token)
            .build()
        responseObserver.onNext(proto)
        responseObserver.onCompleted()
        logger.info("User {} successfully registered", log)
    }

    @Allow(roles = [USER])
    override fun getOnline(
        request: Request.getOnlineRequest?,
        responseObserver: StreamObserver<Request.getOnlineResponse>?
    ) {
        kotlin.runCatching {
            val listOfProtheses = prothesisService.gelAllOnlineProtheses().map { it.id }
            val message = Request.getOnlineResponse.newBuilder().setList(listOfProtheses.toString()).build()
            logger.info("Sending list of prothesis")
            responseObserver?.onNext(message)
            responseObserver?.onCompleted()
        }.onFailure {
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
            logger.error("Error while trying to get list of prothesis. Error: $it")
        }
    }

    @Allow(roles = [USER])
    override fun getSettings(
        request: Request.getSettingsRequest?,
        responseObserver: StreamObserver<Request.getSettingsResponse>?
    ) {
        val id = request?.id
        when {
            id == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                val settingsResponse = Settings.GetSettings.newBuilder()
                val settings = it.settings
                settingsResponse.enableDisplay = settings!!.enableDisplay
                settingsResponse.enableDriver = settings.enableDriver
                settingsResponse.enableEmg = settings.enableEmg
                settingsResponse.enableGyro = settings.enableGyro
                settingsResponse.typeWork = settings.typeWork
                val message = Request.getSettingsResponse.newBuilder().setSettings(settingsResponse.build()).build()
                logger.info("Sending setting for $id")
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
                //todo telemetry?
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
            logger.error("Error while trying to get settings from redis for $id . Error: $it")
        }
    }

    @Allow(roles = [USER])
    override fun setSettings(
        request: Request.setSettingsRequest?,
        responseObserver: StreamObserver<Request.setSettingsResponse>?
    ) {
        val id = request?.id
        val s = request?.settings
        when {
            id == null || s == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                val command = lst.find { it is SetSettings } as MobileWriteApi<Settings.SetSettings>
                command.writeToProsthesis(id, s!!)
                val message = Request.setSettingsResponse.newBuilder().build()
                logger.info("Set settings fot $id")
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
            logger.error("Error while trying to set $id . Error: $it")
        }
    }

    @Allow(roles = [USER])
    override fun getGestures(
        request: Request.getGesturesRequest?,
        responseObserver: StreamObserver<Request.getGesturesResponse>?
    ) {
        val id = request?.id
        when {
            id == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({ it ->
                val gestures = it.gestures!!
                //this piece of shit is due to different object types
                val getGesturesBuilder = Gestures.GetGestures.newBuilder()
                gestures.listGestures.forEach {
                    getGesturesBuilder.addGestures(GestureDto.createFrom(it))
                }
                getGesturesBuilder.lastTimeSync = gestures.lastTimeSync
                val message = Request.getGesturesResponse.newBuilder().setGestures(getGesturesBuilder.build()).build()
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
            logger.error("Error while trying to get gestures for $id . Error: $it")
        }
    }

    @Allow(roles = [USER])
    override fun saveGesture(
        request: Request.saveGestureRequest?,
        responseObserver: StreamObserver<Request.saveGestureResponse>?
    ) {
        val gesture = request?.gesture
        val id = request?.id
        val time = request?.timeSync
        when {
            id == null || gesture == null || time == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                val saveGesture = Gestures.SaveGesture.newBuilder()
                saveGesture.timeSync = time!!
                saveGesture.gesture = gesture
                val command = lst.find { it is SaveGesture } as MobileWriteApi<Gestures.SaveGesture>
                command.writeToProsthesis(id, saveGesture.build())
                logger.info("Saved gesture for $id")
                val message = Request.saveGestureResponse.newBuilder().build()
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            logger.error("Error while trying to save gestures for $id . Error: $it")
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
        }
    }

    @Allow(roles = [USER])
    override fun deleteGesture(
        request: Request.deleteGestureRequest?,
        responseObserver: StreamObserver<Request.deleteGestureResponse>?
    ) {
        val id = request?.id
        val gestureId = request?.gestureId
        val time = request?.timeSync
        when {
            id == null || gestureId == null || time == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                //todo no such gesture
                val deleteGesture = Gestures.DeleteGesture.newBuilder()
                deleteGesture.timeSync = time!!
                deleteGesture.id = gestureId
                val command = lst.find { it is DeleteGesture } as MobileWriteApi<Gestures.DeleteGesture>
                command.writeToProsthesis(id, deleteGesture.build())
                val message = Request.deleteGestureResponse.newBuilder().build()
                logger.info("Deleted gesture $gestureId for $id ")
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            logger.error("Error while trying to delete gesture for $id . Gesture id: $gestureId . Error: $it")
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
        }
    }

    @Allow(roles = [USER])
    override fun performGestureId(
        request: Request.performGestureIdRequest?,
        responseObserver: StreamObserver<Request.performGestureIdResponse>?
    ) {
        val id = request?.id
        val gestureId = request?.gestureId
        when {
            id == null || gestureId == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                //todo check that gesture is present
                val performGestureById = Gestures.PerformGestureById.newBuilder()
                performGestureById.id = gestureId
                val command = lst.find { it is PerformGestureById } as MobileWriteApi<Gestures.PerformGestureById>
                command.writeToProsthesis(id, performGestureById.build())
                val message = Request.performGestureIdResponse.newBuilder().build()
                logger.info(" Performing gesture $gestureId for $id ")
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            logger.error("Error while trying to perform gesture by id for $id . Gesture id: $gestureId . Error: $it")
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
        }
    }

    @Allow(roles = [USER])
    override fun performGestureRaw(
        request: Request.performGestureRawRequest?,
        responseObserver: StreamObserver<Request.performGestureRawResponse>?
    ) {
        val id = request?.id
        val gesture = request?.gesture
        when {
            id == null || gesture == null -> {
                responseObserver?.onError(Status.INVALID_ARGUMENT.asRuntimeException())
            }
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                val performGestureRaw = Gestures.PerformGestureRaw.newBuilder()
                performGestureRaw.gesture = gesture
                val command = lst.find { it is PerformGestureRaw } as MobileWriteApi<Gestures.PerformGestureRaw>
                command.writeToProsthesis(id, performGestureRaw.build())
                val message = Request.performGestureRawResponse.newBuilder().build()
                logger.info(" Perform raw gesture for $id ")
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            logger.error("Error while trying to perform gesture by id for $id . Error: $it")
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
        }

    }

    @Allow(roles = [USER])
    override fun setPositions(
        request: Request.setPositionsRequest?,
        responseObserver: StreamObserver<Request.setPositionsResponse>?
    ) {
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
            checkRights(id) -> {
                responseObserver?.onError(Status.PERMISSION_DENIED.asRuntimeException())
            }
        }
        kotlin.runCatching {
            val prothesis = prothesisService.getProthesisById(id!!)
            prothesis.ifPresentOrElse({
                val setPositions = Gestures.SetPositions.newBuilder()
                setPositions.pointerFingerPosition = pointerPosition!!
                setPositions.middleFingerPosition = middlePosition!!
                setPositions.ringFingerPosition = ringPosition!!
                setPositions.littleFingerPosition = littlePosition!!
                setPositions.thumbFingerPosition = thumbPosition!!
                val command = lst.find { it is SetPositions } as MobileWriteApi<Gestures.SetPositions>
                command.writeToProsthesis(id, setPositions.build())
                val message = Request.setPositionsResponse.newBuilder().build()
                logger.info(" Set positions for $id ")
                responseObserver?.onNext(message)
                responseObserver?.onCompleted()
            }, {
                logger.error("No such prothesis $id")
                responseObserver?.onError(Status.NOT_FOUND.asRuntimeException())
            })
        }.onFailure {
            logger.error("Error while trying to perform gesture by id for $id . Error: $it")
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
        }
    }

    @Allow(roles = [USER])
    override fun setProthesis(
        request: Request.setProthesisRequest?,
        responseObserver: StreamObserver<Request.setProthesisResponse>?
    ) {
        kotlin.runCatching {
            val session = sessionService.getSessionByLogin(request!!.login)
            session!!.prothesisId = request.id
            sessionService.save(session)
        }.onFailure {
            logger.error("Error while trying to set prothesis ${request?.id} for ${request?.login} . Error: $it")
            responseObserver?.onError(Status.UNKNOWN.asRuntimeException())
        }
    }

    private fun checkRights(id: String): Boolean {
        //todo implement
        return id == "norights"
    }
}
