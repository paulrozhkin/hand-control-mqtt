package emulator.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class GestureActionDto(
        val pointerFingerPosition: Byte = 0,
        val middleFingerPosition: Byte = 0,
        val ringFinderPosition: Byte = 0,
        val littleFingerPosition: Byte = 0,
        val thumbFingerPosition: Byte = 0,
        val delay: Short = 0
)