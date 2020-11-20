package emulator.models

@ExperimentalUnsignedTypes
class MqttDataModel(topicMqtt: String, dataMqtt: ByteArray) {
    val topic : String = topicMqtt
    val data : ByteArray = dataMqtt
}