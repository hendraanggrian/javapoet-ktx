package com.hendraanggrian.javapoet.dsl

import com.google.common.truth.Truth.assertThat
import com.hendraanggrian.javapoet.classOf
import com.hendraanggrian.javapoet.parameterSpecOf
import kotlin.test.Test

class ParameterSpecHandlerTest {
    private val list = ParameterSpecHandler(mutableListOf())

    private inline fun container(configuration: ParameterSpecHandlerScope.() -> Unit) =
        ParameterSpecHandlerScope(list).configuration()

    @Test
    fun nativeSpec() {
        list += parameterSpecOf<Parameter1>("parameter1")
        list += listOf(parameterSpecOf<Parameter2>("parameter2"))
        assertThat(list).containsExactly(
            parameterSpecOf<Parameter1>("parameter1"),
            parameterSpecOf<Parameter2>("parameter2")
        )
    }

    @Test
    fun className() {
        val packageName = "com.hendraanggrian.javapoet.dsl.ParameterSpecHandlerTest"
        list.add(packageName.classOf("Parameter1"), "parameter1")
        list["parameter2"] = packageName.classOf("Parameter2")
        container { "parameter3"(packageName.classOf("Parameter3")) { } }
        assertThat(list).containsExactly(
            parameterSpecOf<Parameter1>("parameter1"),
            parameterSpecOf<Parameter2>("parameter2"),
            parameterSpecOf<Parameter3>("parameter3")
        )
    }

    @Test
    fun javaClass() {
        list.add(Parameter1::class.java, "parameter1")
        list["parameter2"] = Parameter2::class.java
        container { "parameter3"(Parameter3::class.java) { } }
        assertThat(list).containsExactly(
            parameterSpecOf<Parameter1>("parameter1"),
            parameterSpecOf<Parameter2>("parameter2"),
            parameterSpecOf<Parameter3>("parameter3")
        )
    }

    @Test
    fun kotlinClass() {
        list.add(Parameter1::class, "parameter1")
        list["parameter2"] = Parameter2::class
        container { "parameter3"(Parameter3::class) { } }
        assertThat(list).containsExactly(
            parameterSpecOf<Parameter1>("parameter1"),
            parameterSpecOf<Parameter2>("parameter2"),
            parameterSpecOf<Parameter3>("parameter3")
        )
    }

    @Test
    fun reifiedType() {
        list.add<Parameter1>("parameter1")
        container { "parameter2"<Parameter2> { } }
        assertThat(list).containsExactly(
            parameterSpecOf<Parameter1>("parameter1"),
            parameterSpecOf<Parameter2>("parameter2")
        )
    }

    class Parameter1
    class Parameter2
    class Parameter3
}