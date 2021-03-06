package com.hendraanggrian.javapoet.dsl

import com.google.common.truth.Truth
import com.squareup.javapoet.TypeName
import com.hendraanggrian.javapoet.asTypeName
import kotlin.test.Test

class TypeNameHandlerTest {
    private val list = TypeNameHandler(mutableListOf())

    @Test
    fun test() {
        list += TypeName.CHAR
        list += Double::class.java
        list += Boolean::class
        list.add<String>()
        Truth.assertThat(list).containsExactly(
            TypeName.CHAR,
            Double::class.java.asTypeName(),
            Boolean::class.asTypeName(),
            String::class.asTypeName()
        )
    }
}