package emulator.models

@ExperimentalUnsignedTypes
class MqttDataModel(topicMqtt: String, dataMqtt: UByteArray) {
    val topic : String = topicMqtt
    val data : UByteArray = dataMqtt
}