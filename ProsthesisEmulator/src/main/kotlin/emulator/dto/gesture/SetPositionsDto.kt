package emulator.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class SetPositionsDto(
        val pointerFingerPosition: Int = 0,
        val middleFingerPosition: Int = 0,
        val ringFinderPosition: Int = 0,
        val littleFingerPosition: Int = 0,
        val thumbFingerPosition: Int = 0,
)