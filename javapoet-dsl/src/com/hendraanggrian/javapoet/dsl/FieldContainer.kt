@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.javapoet.dsl

import com.hendraanggrian.javapoet.FieldSpecBuilder
import com.hendraanggrian.javapoet.JavapoetDslMarker
import com.hendraanggrian.javapoet.buildFieldSpec
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import kotlin.reflect.KClass

abstract class FieldContainer : FieldContainerDelegate {

    inline operator fun plusAssign(spec: FieldSpec) {
        add(spec)
    }

    inline operator fun set(name: String, type: TypeName) {
        add(type, name)
    }

    inline operator fun set(name: String, type: KClass<*>) {
        add(type, name)
    }

    inline fun <reified T> add(name: String, noinline builder: (FieldSpecBuilder.() -> Unit)? = null): FieldSpec =
        add(T::class, name, builder)

    inline operator fun invoke(configuration: FieldContainerScope.() -> Unit) = configuration(FieldContainerScope(this))
}

@JavapoetDslMarker
class FieldContainerScope @PublishedApi internal constructor(private val container: FieldContainer) :
    FieldContainerDelegate {

    override fun add(spec: FieldSpec): FieldSpec = container.add(spec)

    inline operator fun String.invoke(name: TypeName, noinline builder: FieldSpecBuilder.() -> Unit): FieldSpec =
        add(name, this, builder)

    inline operator fun String.invoke(type: KClass<*>, noinline builder: FieldSpecBuilder.() -> Unit): FieldSpec =
        add(type, this, builder)

    inline operator fun <reified T> String.invoke(noinline builder: FieldSpecBuilder.() -> Unit): FieldSpec =
        invoke(T::class, builder)
}

internal interface FieldContainerDelegate {

    fun add(spec: FieldSpec): FieldSpec

    fun add(type: TypeName, name: String, builder: (FieldSpecBuilder.() -> Unit)? = null): FieldSpec =
        add(buildFieldSpec(type, name, builder))

    fun add(type: KClass<*>, name: String, builder: (FieldSpecBuilder.() -> Unit)? = null): FieldSpec =
        add(buildFieldSpec(type, name, builder))
}