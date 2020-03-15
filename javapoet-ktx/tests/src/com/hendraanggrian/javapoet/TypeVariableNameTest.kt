package com.hendraanggrian.javapoet

import kotlin.test.Test
import kotlin.test.assertEquals

class TypeVariableNameTest {

    @Test fun noBounds() = assertEquals("T", "${"T".typeVarOf()}")

    @Test fun classNameBounds() = assertEquals(
        """
            <T extends java.lang.Integer> void go() {
            }
            
            """.trimIndent(),
        "${buildMethodSpec("go") { addTypeVariable("T".typeVarBy(INT.box())) }}"
    )

    @Test fun classBounds() = assertEquals(
        """
            <T extends java.lang.Integer> void go() {
            }
            
            """.trimIndent(),
        "${buildMethodSpec("go") { addTypeVariable("T".typeVarBy(java.lang.Integer::class.java)) }}"
    )

    @Test fun kclassBounds() = assertEquals(
        """
            <T extends java.lang.Integer> void go() {
            }
            
            """.trimIndent(),
        "${buildMethodSpec("go") { addTypeVariable("T".typeVarBy(java.lang.Integer::class)) }}"
    )
}