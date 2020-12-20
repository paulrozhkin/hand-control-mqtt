package com.handcontrol.server.grpc

import com.handcontrol.server.mqtt.command.dto.TelemetryDto
import com.handcontrol.server.mqtt.command.get.GetTelemetry
import com.handcontrol.server.protobuf.Stream
import com.handcontrol.server.protobuf.TelemetryStreamGrpcKt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Autowired

@GRpcService
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class TelemetryStreamImpl : TelemetryStreamGrpcKt.TelemetryStreamCoroutineImplBase() {

    @Autowired
    private lateinit var getTelemetry: GetTelemetry

    @FlowPreview
    override fun startTelemetryStream(request: Stream.SubRequest): Flow<Stream.PubReply> {
        val id = request.id
        getTelemetry.subscribe(id)
        val channel = getTelemetry.getChannel(id)

        return flow {
            while (true) {
                val receive = channel.receive()

                val buildTelemetry = Stream.PubReply.newBuilder()
                    .setTelemetry(TelemetryDto.createFrom(receive))
                    .build()
                emit(buildTelemetry)
            }
        }
    }
}