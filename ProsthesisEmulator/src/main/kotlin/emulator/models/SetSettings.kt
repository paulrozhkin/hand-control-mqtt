package emulator.models

import emulator.models.enums.TypeWork
import emulator.services.ByteToBooleanConverter

@ExperimentalUnsignedTypes
class SetSettings(val typeWork: TypeWork, val telemetryFrequency: UShort,
                  val enableEmg: Boolean, val enableDisplay: Boolean,
                  val enableGyro: Boolean, val enableDriver: Boolean) {

    fun serialize(): UByteArray {
        val freqFirstByte = (telemetryFrequency and 0xFFu).toUByte()
        val freqSecondByte = ((telemetryFrequency and 0xFF00u).toInt() shr 8).toUByte()
        return ubyteArrayOf(typeWork.serialize(), freqFirstByte, freqSecondByte,
                ByteToBooleanConverter.convertBack(enableEmg),
                ByteToBooleanConverter.convertBack(enableDisplay),
                ByteToBooleanConverter.convertBack(enableGyro),
                ByteToBooleanConverter.convertBack(enableDriver))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SetSettings

        if (typeWork != other.typeWork) return false
        if (telemetryFrequency != other.telemetryFrequency) return false
        if (enableEmg != other.enableEmg) return false
        if (enableDisplay != other.enableDisplay) return false
        if (enableGyro != other.enableGyro) return false
        if (enableDriver != other.enableDriver) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeWork.hashCode()
        result = 31 * result + telemetryFrequency.hashCode()
        result = 31 * result + enableEmg.hashCode()
        result = 31 * result + enableDisplay.hashCode()
        result = 31 * result + enableGyro.hashCode()
        result = 31 * result + enableDriver.hashCode()
        return result
    }

    companion object Factory {
        fun deserialize(byteArray: UByteArray): SetSettings {
            if (byteArray.size != 7) {
                throw IllegalArgumentException("ByteArray must be 7 bytes")
            }

            val typeWork = TypeWork.deserialize(byteArray[0])
            val telemetryFrequency = byteArray[1].toUShort() or ((byteArray[2]).toUInt() shl 8).toUShort()
            val enableEmg = ByteToBooleanConverter.convert(byteArray[3])
            val enableDisplay = ByteToBooleanConverter.convert(byteArray[4])
            val enableGyro = ByteToBooleanConverter.convert(byteArray[5])
            val enableDriver = ByteToBooleanConverter.convert(byteArray[6])

            return SetSettings(typeWork, telemetryFrequency, enableEmg, enableDisplay, enableGyro, enableDriver)
        }
    }
}