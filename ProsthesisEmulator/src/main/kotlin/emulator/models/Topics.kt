package emulator.models

object Topics {
    const val SetOnline = "controllers/online";
    const val Offline = "controllers/offline";
    const val Telemetry = "data/telemetry";
    const val GetSettings = "data/settings";
    const val SetSettings = "action/settings";
    const val GetGestures = "data/gestures";
    const val SaveGesture = "action/gestures";
    const val DeleteGesture = "action/gestures/remove";
    const val PerformGestureId = "action/performGestureId";
    const val PerformGestureRaw = "action/performGestureRaw";
    const val SetPositions = "action/positions";
}