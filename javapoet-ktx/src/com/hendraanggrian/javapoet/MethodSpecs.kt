package com.hendraanggrian.javapoet

import com.hendraanggrian.javapoet.dsl.AnnotationContainer
import com.hendraanggrian.javapoet.dsl.CodeCollection
import com.hendraanggrian.javapoet.dsl.JavadocContainer
import com.hendraanggrian.javapoet.dsl.ParameterContainer
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

/** Builds a new [MethodSpec] from [name]. */
fun buildMethod(name: String): MethodSpec =
    MethodSpecBuilder(MethodSpec.methodBuilder(name)).build()

/**
 * Builds a new [MethodSpec] from [name],
 * by populating newly created [MethodSpecBuilder] using provided [builderAction] and then building it.
 */
inline fun buildMethod(name: String, builderAction: MethodSpecBuilder.() -> Unit): MethodSpec =
    MethodSpecBuilder(MethodSpec.methodBuilder(name)).apply(builderAction).build()

/** Builds a new constructor [MethodSpec]. */
fun buildConstructorMethod(): MethodSpec =
    MethodSpecBuilder(MethodSpec.constructorBuilder()).build()

/**
 * Builds a new constructor [MethodSpec],
 * by populating newly created [MethodSpecBuilder] using provided [builderAction] and then building it.
 */
inline fun buildConstructorMethod(builderAction: MethodSpecBuilder.() -> Unit): MethodSpec =
    MethodSpecBuilder(MethodSpec.constructorBuilder()).apply(builderAction).build()

/** Wrapper of [MethodSpec.Builder], providing DSL support as a replacement to Java builder. */
@JavapoetDslMarker
class MethodSpecBuilder @PublishedApi internal constructor(private val nativeBuilder: MethodSpec.Builder) :
    CodeCollection() {

    val javadoc: JavadocContainer = object : JavadocContainer() {
        override fun append(format: String, vararg args: Any): Unit =
            format.formatWith(args) { s, array -> nativeBuilder.addJavadoc(s, *array) }

        override fun append(block: CodeBlock): CodeBlock =
            block.also { nativeBuilder.addJavadoc(it) }
    }

    val annotations: AnnotationContainer = object : AnnotationContainer() {
        override fun add(spec: AnnotationSpec): AnnotationSpec =
            spec.also { nativeBuilder.addAnnotation(it) }
    }

    fun addModifiers(vararg modifiers: Modifier) {
        nativeBuilder.addModifiers(*modifiers)
    }

    fun addModifiers(modifiers: Iterable<Modifier>) {
        nativeBuilder.addModifiers(modifiers)
    }

    fun addTypeVariables(typeVariables: Iterable<TypeVariableName>) {
        nativeBuilder.addTypeVariables(typeVariables)
    }

    fun addTypeVariable(typeVariable: TypeVariableName) {
        nativeBuilder.addTypeVariable(typeVariable)
    }

    var returns: TypeName
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            nativeBuilder.returns(value)
        }

    fun returns(type: KClass<*>) {
        nativeBuilder.returns(type.java)
    }

    inline fun <reified T> returns() =
        returns(T::class)

    val parameters: ParameterContainer = object : ParameterContainer() {
        override fun add(spec: ParameterSpec): ParameterSpec =
            spec.also { nativeBuilder.addParameter(it) }
    }

    var varargs: Boolean
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            nativeBuilder.varargs(value)
        }

    fun addExceptions(types: Iterable<TypeName>) {
        nativeBuilder.addExceptions(types)
    }

    fun addException(type: TypeName) {
        nativeBuilder.addException(type)
    }

    fun addException(type: KClass<*>) {
        nativeBuilder.addException(type.java)
    }

    inline fun <reified T> addException() =
        addException(T::class)

    override fun append(format: String, vararg args: Any): Unit =
        format.formatWith(args) { s, array -> nativeBuilder.addCode(s, *array) }

    override fun append(block: CodeBlock): CodeBlock =
        block.also { nativeBuilder.addCode(it) }

    override fun beginFlow(flow: String, vararg args: Any): Unit =
        flow.formatWith(args) { s, array -> nativeBuilder.beginControlFlow(s, *array) }

    override fun nextFlow(flow: String, vararg args: Any): Unit =
        flow.formatWith(args) { s, array -> nativeBuilder.nextControlFlow(s, *array) }

    override fun endFlow() {
        nativeBuilder.endControlFlow()
    }

    override fun endFlow(flow: String, vararg args: Any): Unit =
        flow.formatWith(args) { s, array -> nativeBuilder.endControlFlow(s, *array) }

    override fun appendln(format: String, vararg args: Any): Unit =
        format.formatWith(args) { s, array -> nativeBuilder.addStatement(s, *array) }

    override fun appendln(block: CodeBlock): CodeBlock =
        block.also { nativeBuilder.addStatement(it) }

    fun addNamedCode(format: String, args: Map<String, *>): Unit =
        format.formatWith(args) { s, map -> nativeBuilder.addNamedCode(s, map) }

    fun addComment(format: String, vararg args: Any): Unit =
        format.formatWith(args) { s, array -> nativeBuilder.addComment(s, *array) }

    fun defaultValue(format: String, vararg args: Any): Unit =
        format.formatWith(args) { s, array -> nativeBuilder.defaultValue(s, *array) }

    inline var defaultValue: String
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) = defaultValue(value)

    fun defaultValue(block: CodeBlock): CodeBlock =
        block.also { nativeBuilder.defaultValue(it) }

    inline fun defaultValue(builderAction: CodeBlockBuilder.() -> Unit): CodeBlock =
        defaultValue(buildCode(builderAction))

    fun build(): MethodSpec =
        nativeBuilder.build()
}