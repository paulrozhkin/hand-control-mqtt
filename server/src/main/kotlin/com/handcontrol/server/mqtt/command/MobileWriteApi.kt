package com.handcontrol.server.mqtt.command

/**
 *  an interface for commands that support writing grpc objects from mobile to mqtt.
 */
interface MobileWriteApi<T> {

    fun writeToProsthesis(id: String, grpcObj: T)

}