package emulator.dto.gesture

import emulator.dto.UuidDto
import kotlinx.serialization.Serializable

@Serializable
data class DeleteGestureDto(
        val timeSync: Long = 0,
        val id: UuidDto = UuidDto(""),
)