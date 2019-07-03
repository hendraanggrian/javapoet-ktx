@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.javapoet

import com.hendraanggrian.javapoet.dsl.AnnotationContainer
import com.hendraanggrian.javapoet.dsl.CodeContainer
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

inline fun buildFieldSpec(
    type: TypeName,
    name: String,
    noinline builder: (FieldSpecBuilder.() -> Unit)? = null
): FieldSpec = FieldSpecBuilder(FieldSpec.builder(type, name))
    .also { builder?.invoke(it) }
    .build()

inline fun buildFieldSpec(
    type: KClass<*>,
    name: String,
    noinline builder: (FieldSpecBuilder.() -> Unit)? = null
): FieldSpec = FieldSpecBuilder(FieldSpec.builder(type.java, name))
    .also { builder?.invoke(it) }
    .build()

inline fun <reified T> buildFieldSpec(
    name: String,
    noinline builder: (FieldSpecBuilder.() -> Unit)? = null
): FieldSpec = buildFieldSpec(T::class, name, builder)

class FieldSpecBuilder @PublishedApi internal constructor(private val nativeBuilder: FieldSpec.Builder) {

    val javadoc: CodeContainer = object : CodeContainer() {
        override fun add(format: String, vararg args: Any) {
            nativeBuilder.addJavadoc(format, *args)
        }

        override fun add(block: CodeBlock): CodeBlock = block.also { nativeBuilder.addJavadoc(it) }
    }

    val annotations: AnnotationContainer = object : AnnotationContainer() {
        override fun add(spec: AnnotationSpec): AnnotationSpec = spec.also { nativeBuilder.addAnnotation(it) }
    }

    fun addModifiers(vararg modifiers: Modifier) {
        nativeBuilder.addModifiers(*modifiers)
    }

    fun initializer(format: String, vararg args: Any) {
        nativeBuilder.initializer(format, *args)
    }

    inline var initializer: String
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) = initializer(value)

    fun initializer(block: CodeBlock) {
        nativeBuilder.initializer(block)
    }

    inline fun initializer(builder: CodeBlockBuilder.() -> Unit) = initializer(buildCodeBlock(builder))

    fun build(): FieldSpec = nativeBuilder.build()
}