package emulator.models

import emulator.models.enums.TypeWork
import emulator.services.ByteToBooleanConverter

@ExperimentalUnsignedTypes
class GetSettings constructor(val typeWork: TypeWork, val enableEmg: Boolean, val enableDisplay: Boolean,
                              val enableGyro: Boolean, val enableDriver: Boolean) {

    fun serialize(): UByteArray {
        return ubyteArrayOf(typeWork.serialize(),
                ByteToBooleanConverter.convertBack(enableEmg),
                ByteToBooleanConverter.convertBack(enableDisplay),
                ByteToBooleanConverter.convertBack(enableGyro),
                ByteToBooleanConverter.convertBack(enableDriver))
    }

    companion object Factory {
        fun deserialize(byteArray: UByteArray): GetSettings {
            if (byteArray.size != 5) {
                throw IllegalArgumentException("ByteArray must be 7 bytes")
            }

            val typeWork = TypeWork.deserialize(byteArray[0])
            val enableEmg = ByteToBooleanConverter.convert(byteArray[3])
            val enableDisplay = ByteToBooleanConverter.convert(byteArray[4])
            val enableGyro = ByteToBooleanConverter.convert(byteArray[5])
            val enableDriver = ByteToBooleanConverter.convert(byteArray[6])

            return GetSettings(typeWork, enableEmg, enableDisplay, enableGyro, enableDriver)
        }
    }
}