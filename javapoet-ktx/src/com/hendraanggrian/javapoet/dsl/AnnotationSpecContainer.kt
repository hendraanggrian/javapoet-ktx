package com.hendraanggrian.javapoet.dsl

import com.hendraanggrian.javapoet.AnnotationSpecBuilder
import com.hendraanggrian.javapoet.JavapoetDslMarker
import com.hendraanggrian.javapoet.buildAnnotation
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import kotlin.reflect.KClass

/** An [AnnotationSpecContainer] is responsible for managing a set of annotation instances. */
abstract class AnnotationSpecContainer internal constructor() {

    /** Add annotation to this container. */
    abstract fun add(spec: AnnotationSpec)

    /** Add annotation from [type], returning the annotation added. */
    fun add(type: ClassName): AnnotationSpec = buildAnnotation(type).also { add(it) }

    /** Add annotation from [type] with custom initialization [builderAction], returning the annotation added. */
    inline fun add(type: ClassName, builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        buildAnnotation(type, builderAction).also { add(it) }

    /** Add annotation from [type], returning the annotation added. */
    fun add(type: Class<*>): AnnotationSpec = buildAnnotation(type).also { add(it) }

    /** Add annotation from [type] with custom initialization [builderAction], returning the annotation added. */
    inline fun add(type: Class<*>, builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        buildAnnotation(type, builderAction).also { add(it) }

    /** Add annotation from [type], returning the annotation added. */
    fun add(type: KClass<*>): AnnotationSpec = buildAnnotation(type).also { add(it) }

    /** Add annotation from [type] with custom initialization [builderAction], returning the annotation added. */
    inline fun add(type: KClass<*>, builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        buildAnnotation(type, builderAction).also { add(it) }

    /** Add annotation from reified [T], returning the annotation added. */
    inline fun <reified T> add(): AnnotationSpec = buildAnnotation<T>().also { add(it) }

    /** Add annotation from reified [T] with custom initialization [builderAction], returning the annotation added. */
    inline fun <reified T> add(builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        buildAnnotation<T>(builderAction).also { add(it) }

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(spec: AnnotationSpec) = add(spec)

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(type: ClassName) {
        add(type)
    }

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(type: Class<*>) {
        add(type)
    }

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(type: KClass<*>) {
        add(type)
    }
}

/** Receiver for the `annotations` function type providing an extended set of operators for the configuration. */
@JavapoetDslMarker
class AnnotationSpecContainerScope @PublishedApi internal constructor(private val container: AnnotationSpecContainer) :
    AnnotationSpecContainer() {

    override fun add(spec: AnnotationSpec) = container.add(spec)

    /** Convenient method to add annotation with receiver type. */
    inline operator fun ClassName.invoke(builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        add(this, builderAction)

    /** Convenient method to add annotation with receiver type. */
    inline operator fun Class<*>.invoke(builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        add(this, builderAction)

    /** Convenient method to add annotation with receiver type. */
    inline operator fun KClass<*>.invoke(builderAction: AnnotationSpecBuilder.() -> Unit): AnnotationSpec =
        add(this, builderAction)
}
