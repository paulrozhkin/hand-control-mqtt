package com.handcontrol.server.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoBufBuilder

@ExperimentalSerializationApi
class ProtobufSerializer {

    companion object {
        inline fun <reified T> serialize(obj: T): ByteArray {
            return ProtoBuf { ProtoBufBuilder::encodeDefaults.set(this, false) }.encodeToByteArray(obj)
        }

        inline fun <reified T> deserialize(arr: ByteArray): T {
            return ProtoBuf.decodeFromByteArray(arr)
        }
    }
}