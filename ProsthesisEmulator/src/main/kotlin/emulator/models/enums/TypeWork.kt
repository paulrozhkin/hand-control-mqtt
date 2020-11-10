package emulator.models.enums

import java.lang.Exception

@ExperimentalUnsignedTypes
enum class TypeWork(val value: UByte){
    MIO(0x00.toUByte()),
    COMMANDS(0x01.toUByte()),
    AUTO(0x02.toUByte());

    fun serialize(): UByte {
        return this.value;
    }

    companion object Factory {
        fun deserialize(value: UByte): TypeWork {
            return when (value) {
                MIO.value -> MIO
                AUTO.value -> AUTO
                COMMANDS.value -> COMMANDS
                else -> throw IllegalArgumentException()
            }
        }
    }
}