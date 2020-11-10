package emulator.models

import java.util.*

class Gesture(uuid: UUID, name: String, lastTimeSync: Long, iterableGesture: Boolean,
              NumberOfGestureRepetitions: UByte, ListActions: List<GestureAction>) {
    val NumberOfMotions: UByte = ListActions.size.toUByte();
}