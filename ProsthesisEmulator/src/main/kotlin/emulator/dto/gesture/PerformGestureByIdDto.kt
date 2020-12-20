package emulator.dto.gesture

import emulator.dto.UuidDto
import kotlinx.serialization.Serializable

@Serializable
data class PerformGestureByIdDto(
        val id: UuidDto = UuidDto(""),
)