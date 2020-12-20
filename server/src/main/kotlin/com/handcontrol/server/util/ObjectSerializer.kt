package com.handcontrol.server.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ObjectSerializer {

    companion object {
        fun serialize(obj: Any): ByteArray {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(obj)
            oos.flush()

            val result = baos.toByteArray()
            baos.close()
            oos.close()
            return result
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> deserialize(arr: ByteArray): T {
            val bais = ByteArrayInputStream(arr)
            val ois = ObjectInputStream(bais)

            val result = ois.readObject() as T
            ois.close()
            bais.close()
            return result
        }
    }
}