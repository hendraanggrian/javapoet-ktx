package io.github.hendraanggrian.javapoet.dsl

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import io.github.hendraanggrian.javapoet.AnnotationSpecBuilder
import io.github.hendraanggrian.javapoet.SpecDslMarker
import io.github.hendraanggrian.javapoet.annotationSpecOf
import io.github.hendraanggrian.javapoet.buildAnnotationSpec
import kotlin.reflect.KClass

/** An [AnnotationSpecHandler] is responsible for managing a set of annotation instances. */
open class AnnotationSpecHandler internal constructor(actualList: MutableList<AnnotationSpec>) :
    MutableList<AnnotationSpec> by actualList {

    /** Add annotation from [ClassName]. */
    fun add(type: ClassName): Boolean = add(annotationSpecOf(type))

    /** Add annotation from [Class]. */
    fun add(type: Class<*>): Boolean = add(annotationSpecOf(type))

    /** Add annotation from [KClass]. */
    fun add(type: KClass<*>): Boolean = add(annotationSpecOf(type))

    /** Add annotation from [T]. */
    inline fun <reified T> add(): Boolean = add(annotationSpecOf<T>())

    /** Add annotation from [ClassName] with custom initialization [configuration]. */
    inline fun add(
        type: ClassName,
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(buildAnnotationSpec(type, configuration))

    /** Add annotation from [Class] with custom initialization [configuration]. */
    inline fun add(
        type: Class<*>,
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(buildAnnotationSpec(type, configuration))

    /** Add annotation from [KClass] with custom initialization [configuration]. */
    inline fun add(
        type: KClass<*>,
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(buildAnnotationSpec(type, configuration))

    /** Add annotation from [T] with custom initialization [configuration]. */
    inline fun <reified T> add(
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(buildAnnotationSpec<T>(configuration))

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(type: ClassName): Unit = plusAssign(annotationSpecOf(type))

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(type: Class<*>): Unit = plusAssign(annotationSpecOf(type))

    /** Convenient method to add annotation with operator function. */
    operator fun plusAssign(type: KClass<*>): Unit = plusAssign(annotationSpecOf(type))
}

/** Receiver for the `annotations` function type providing an extended set of operators for the configuration. */
@SpecDslMarker
class AnnotationSpecHandlerScope(actualList: MutableList<AnnotationSpec>) : AnnotationSpecHandler(actualList) {

    /** Convenient method to add annotation with receiver type. */
    inline operator fun ClassName.invoke(
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(this, configuration)

    /** Convenient method to add annotation with receiver type. */
    inline operator fun Class<*>.invoke(
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(this, configuration)

    /** Convenient method to add annotation with receiver type. */
    inline operator fun KClass<*>.invoke(
        configuration: AnnotationSpecBuilder.() -> Unit
    ): Boolean = add(this, configuration)
}