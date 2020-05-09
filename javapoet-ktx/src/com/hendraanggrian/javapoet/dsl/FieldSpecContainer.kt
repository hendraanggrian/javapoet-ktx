package com.hendraanggrian.javapoet.dsl

import com.hendraanggrian.javapoet.FieldSpecBuilder
import com.hendraanggrian.javapoet.JavapoetDslMarker
import com.hendraanggrian.javapoet.buildFieldSpec
import com.hendraanggrian.javapoet.fieldSpecOf
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import java.lang.reflect.Type
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

private interface FieldSpecAddable {

    /** Add field to this container. */
    fun add(spec: FieldSpec)

    /** Add collection of fields to this container. */
    fun addAll(specs: Iterable<FieldSpec>): Boolean
}

/** A [FieldSpecContainer] is responsible for managing a set of field instances. */
abstract class FieldSpecContainer : FieldSpecAddable {

    /** Add field from [type] and [name], returning the field added. */
    fun add(type: TypeName, name: String, vararg modifiers: Modifier): FieldSpec =
        fieldSpecOf(type, name, *modifiers).also { add(it) }

    /** Add field from [type] and [name] with custom initialization [builderAction], returning the field added. */
    inline fun add(
        type: TypeName,
        name: String,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = buildFieldSpec(type, name, *modifiers, builderAction = builderAction).also { add(it) }

    /** Add field from [type] and [name], returning the field added. */
    fun add(type: Type, name: String, vararg modifiers: Modifier): FieldSpec =
        fieldSpecOf(type, name, *modifiers).also { add(it) }

    /** Add field from [type] and [name] with custom initialization [builderAction], returning the field added. */
    inline fun add(
        type: Type,
        name: String,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = buildFieldSpec(type, name, *modifiers, builderAction = builderAction).also { add(it) }

    /** Add field from [type] and [name], returning the field added. */
    fun add(type: KClass<*>, name: String, vararg modifiers: Modifier): FieldSpec =
        fieldSpecOf(type, name, *modifiers).also { add(it) }

    /** Add field from [type] and [name] with custom initialization [builderAction], returning the field added. */
    inline fun add(
        type: KClass<*>,
        name: String,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = buildFieldSpec(type, name, *modifiers, builderAction = builderAction).also { add(it) }

    /** Add field from reified [T] and [name], returning the field added. */
    inline fun <reified T> add(name: String, vararg modifiers: Modifier): FieldSpec =
        fieldSpecOf<T>(name, *modifiers).also { add(it) }

    /** Add field from reified [T] and [name] with custom initialization [builderAction], returning the field added. */
    inline fun <reified T> add(
        name: String,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = buildFieldSpec<T>(name, *modifiers, builderAction = builderAction).also { add(it) }

    /** Convenient method to add field with operator function. */
    operator fun plusAssign(spec: FieldSpec): Unit = add(spec)

    /** Convenient method to add collection of fields with operator function. */
    operator fun plusAssign(specs: Iterable<FieldSpec>) {
        addAll(specs)
    }

    /** Convenient method to add field with operator function. */
    operator fun set(name: String, type: TypeName) {
        add(type, name)
    }

    /** Convenient method to add field with operator function. */
    operator fun set(name: String, type: Type) {
        add(type, name)
    }

    /** Convenient method to add field with operator function. */
    operator fun set(name: String, type: KClass<*>) {
        add(type, name)
    }
}

/** Receiver for the `fields` function type providing an extended set of operators for the configuration. */
@JavapoetDslMarker
class FieldSpecContainerScope(container: FieldSpecContainer) : FieldSpecContainer(),
    FieldSpecAddable by container {

    /** Convenient method to add field with receiver type. */
    inline operator fun String.invoke(
        type: TypeName,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = add(type, this, *modifiers, builderAction = builderAction)

    /** Convenient method to add field with receiver type. */
    inline operator fun String.invoke(
        type: Type,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = add(type, this, *modifiers, builderAction = builderAction)

    /** Convenient method to add field with receiver type. */
    inline operator fun String.invoke(
        type: KClass<*>,
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = add(type, this, *modifiers, builderAction = builderAction)

    /** Convenient method to add field with receiver type. */
    inline operator fun <reified T> String.invoke(
        vararg modifiers: Modifier,
        builderAction: FieldSpecBuilder.() -> Unit
    ): FieldSpec = add<T>(this, *modifiers, builderAction = builderAction)
}
