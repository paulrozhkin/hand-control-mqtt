package com.handcontrol.server.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class ObjectSerializerTest {

    @ParameterizedTest
    @MethodSource("provideObjects")
    fun <T : Any> serializeAndDeserialize(expected: T) {
        val bytes = ObjectSerializer.serialize(expected)

        val actual = ObjectSerializer.deserialize<T>(bytes)

        assertEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        fun provideObjects() = listOf(
                Arguments.of("TEMP_STRING"),
        )
    }

}