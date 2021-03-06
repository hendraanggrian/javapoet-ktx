package com.hendraanggrian.javapoet

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

class MethodSpecBuilderTest {
    private val expected = MethodSpec.methodBuilder("main")
        .addJavadoc("firstJavadoc")
        .addJavadoc(
            CodeBlock.builder()
                .add("secondJavadoc")
                .build()
        )
        .addAnnotation(AnnotationSpec.builder(Deprecated::class.java).build())
        .addModifiers(PUBLIC, STATIC)
        .returns(VOID)
        .addParameter(ParameterSpec.builder(Array<String>::class.java, "param").build())
        .varargs(true)
        .addException(IOException::class.java)
        .addComment("Some comment")
        .addCode("doSomething()")
        .build()

    @Test
    fun simple() {
        assertEquals(expected, buildMethodSpec("main") {
            javadoc.append("firstJavadoc")
            javadoc.append {
                append("secondJavadoc")
            }
            annotations.add<Deprecated>()
            addModifiers(PUBLIC, STATIC)
            returns = VOID
            parameters.add<Array<String>>("param")
            isVarargs = true
            addException<IOException>()
            addComment("Some comment")
            append("doSomething()")
        })
    }

    @Test
    fun invocation() {
        assertEquals(expected, buildMethodSpec("main") {
            javadoc {
                append("firstJavadoc")
                append {
                    append("secondJavadoc")
                }
            }
            annotations {
                add<Deprecated>()
            }
            addModifiers(PUBLIC, STATIC)
            returns = VOID
            parameters {
                add<Array<String>>("param")
            }
            isVarargs = true
            addException<IOException>()
            addComment("Some comment")
            append("doSomething()")
        })
    }
}