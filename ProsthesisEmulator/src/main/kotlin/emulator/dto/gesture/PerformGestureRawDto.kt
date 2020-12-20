package emulator.dto.gesture

import kotlinx.serialization.Serializable

@Serializable
data class PerformGestureRawDto(
        val gesture: GestureDto
)