package com.handcontrol.server.util

import com.handcontrol.server.mqtt.command.dto.TelemetryDto
import com.handcontrol.server.mqtt.command.dto.UuidDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureActionDto
import com.handcontrol.server.mqtt.command.dto.gesture.GestureDto
import com.handcontrol.server.mqtt.command.dto.gesture.GetGesturesDto
import com.handcontrol.server.mqtt.command.dto.settings.SetSettingsDto
import com.handcontrol.server.protobuf.Enums.DriverStatusType.DRIVER_STATUS_SLEEP
import com.handcontrol.server.protobuf.Enums.ModeType
import com.handcontrol.server.protobuf.Enums.ModuleStatusType.MODULE_STATUS_CONNECTION_ERROR
import com.handcontrol.server.protobuf.Enums.ModuleStatusType.MODULE_STATUS_DISABLED
import com.handcontrol.server.protobuf.Enums.ModuleStatusType.MODULE_STATUS_WORK
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

/**
 * Not real tests. Used as examples of usage
 */
@ExperimentalSerializationApi
class ProtobufSerializerTest {

    @Test
    fun testProtobufSerializer() {
        val setSettings = SetSettingsDto(ModeType.MODE_COMMANDS, 233)

        val encodedSettings = ProtobufSerializer.serialize(setSettings)
        encodedSettings.printAsHex()

        val decodedSettings = ProtobufSerializer.deserialize<SetSettingsDto>(encodedSettings)
        println(decodedSettings)

        val dt = LocalDateTime.of(2020, 11, 11, 10, 54)
        val unixTime = dt.toEpochSecond(ZoneOffset.UTC)
        val id = UUID.randomUUID().toString()

        val telemetry = TelemetryDto(40,
                MODULE_STATUS_WORK, MODULE_STATUS_DISABLED, MODULE_STATUS_CONNECTION_ERROR, DRIVER_STATUS_SLEEP,
                unixTime, 1, UuidDto(id),
                1, 2, 3, 4, 5, 6
        )

        val encodedTelemetry = ProtobufSerializer.serialize(telemetry)
        encodedTelemetry.printAsHex()

        val decodedTelemetry = ProtobufSerializer.deserialize<TelemetryDto>(encodedTelemetry)
        println(decodedTelemetry)

        val a1 = GestureActionDto(1, 1, 1, 1, 1, 1)
        val a2 = GestureActionDto(2, 2, 2, 2, 2, 1)
        val g1 = GestureDto(UuidDto("1"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val g2 = GestureDto(UuidDto("2"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val getGestures = GetGesturesDto(unixTime, listGestures = listOf(g1, g2))

        val encodedGestures = ProtobufSerializer.serialize(getGestures)
        encodedGestures.printAsHex()

        val decodedGestures = ProtobufSerializer.deserialize<GetGesturesDto>(encodedGestures)
        println(decodedGestures)
    }

    @Test
    fun testProtoDeserializer() {
        val arr = ubyteArrayOf(0x08u, 0x28u, 0x10u, 0x01u, 0x18u, 0x04u, 0x20u, 0x03u, 0x28u, 0x04u, 0x30u, 0x87u, 0xccu, 0xaeu, 0xfdu, 0x05u, 0x38u, 0x01u, 0x42u, 0x26u, 0x0au, 0x24u, 0x39u, 0x37u, 0x66u, 0x37u, 0x32u, 0x64u, 0x38u, 0x34u, 0x2du, 0x62u, 0x38u, 0x35u, 0x36u, 0x2du, 0x34u, 0x39u, 0x33u, 0x34u, 0x2du, 0x38u, 0x64u, 0x38u, 0x30u, 0x2du, 0x32u, 0x62u, 0x33u, 0x66u, 0x66u, 0x35u, 0x61u, 0x64u, 0x34u, 0x31u, 0x64u, 0x35u, 0x48u, 0x01u, 0x50u, 0x02u, 0x58u, 0x03u, 0x60u, 0x04u, 0x68u, 0x05u, 0x70u, 0x06u)

        val decoder = ProtobufSerializer.deserialize<TelemetryDto>(arr.toByteArray())
        println(decoder)

    }

    @Test
    fun testProtoEmpty() {
        val telemetry = TelemetryDto(displayStatus = MODULE_STATUS_DISABLED, pointerFingerPosition = 3)
        val encoded = ProtobufSerializer.serialize(telemetry)
        encoded.printAsHex()

        val decode = ProtobufSerializer.deserialize<TelemetryDto>(encoded)
        println(decode)

        val encoded2 = ProtobufSerializer.serialize(decode)
        encoded2.printAsHex()
    }

    @Test
    fun testProtoArray() {
        val arr2 = ubyteArrayOf(0x08u, 0xc3u, 0xe8u, 0xaeu, 0xfdu, 0x05u,
                0x12u, 0x36u, 0x0au, 0x03u, 0x0au, 0x01u, 0x31u, 0x12u, 0x0cu, 0x6eu, 0x61u, 0x6du, 0x65u, 0x5fu, 0x67u, 0x65u, 0x73u, 0x74u, 0x75u, 0x72u, 0x65u, 0x18u, 0x9bu, 0x01u, 0x20u, 0x01u, 0x28u, 0x06u,
                0x32u, 0x0cu, 0x08u, 0x01u, 0x10u, 0x01u, 0x18u, 0x01u, 0x20u, 0x01u, 0x28u, 0x01u, 0x30u, 0x01u,
                0x32u, 0x0cu, 0x08u, 0x02u, 0x10u, 0x02u, 0x18u, 0x02u, 0x20u, 0x02u, 0x28u, 0x02u, 0x30u, 0x01u,
                0x12u, 0x36u, 0x0au, 0x03u, 0x0au, 0x01u, 0x32u, 0x12u, 0x0cu, 0x6eu, 0x61u, 0x6du, 0x65u, 0x5fu, 0x67u, 0x65u, 0x73u, 0x74u, 0x75u, 0x72u, 0x65u, 0x18u, 0x9bu, 0x01u, 0x20u, 0x01u, 0x28u, 0x06u,
                0x32u, 0x0cu, 0x08u, 0x01u, 0x10u, 0x01u, 0x18u, 0x01u, 0x20u, 0x01u, 0x28u, 0x01u, 0x30u, 0x01u,
                0x32u, 0x0cu, 0x08u, 0x02u, 0x10u, 0x02u, 0x18u, 0x02u, 0x20u, 0x02u, 0x28u, 0x02u, 0x30u, 0x01u)


        val decoder1 = ProtobufSerializer.deserialize<GetGesturesDto>(arr2.toByteArray())
        println(decoder1)

        val unixTime = 1605088323L

        val a1 = GestureActionDto(1, 1, 1, 1, 1, 1)
        val a2 = GestureActionDto(2, 2, 2, 2, 2, 1)
        val g1 = GestureDto(UuidDto("1"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val g2 = GestureDto(UuidDto("2"), "name_gesture", 155L, true, 6, listActions = listOf(a1, a2))
        val getGestures = GetGesturesDto(unixTime, listGestures = listOf(g1, g2))
        val encoded = ProtobufSerializer.serialize(getGestures)
        encoded.printAsHex()

        val decoder = ProtoBuf.decodeFromByteArray<GetGesturesDto>(encoded)
        println(decoder)
    }

    private fun ByteArray.printAsHex() = this.forEach { val st = String.format("%02X ", it); print(st) }
}