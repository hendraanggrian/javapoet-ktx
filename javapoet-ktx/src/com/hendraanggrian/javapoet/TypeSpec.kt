package com.hendraanggrian.javapoet

import com.hendraanggrian.javapoet.dsl.AnnotationSpecHandler
import com.hendraanggrian.javapoet.dsl.AnnotationSpecHandlerScope
import com.hendraanggrian.javapoet.dsl.FieldSpecHandler
import com.hendraanggrian.javapoet.dsl.FieldSpecHandlerScope
import com.hendraanggrian.javapoet.dsl.JavadocHandler
import com.hendraanggrian.javapoet.dsl.JavadocHandlerScope
import com.hendraanggrian.javapoet.dsl.MethodSpecHandler
import com.hendraanggrian.javapoet.dsl.MethodSpecHandlerScope
import com.hendraanggrian.javapoet.dsl.TypeNameHandler
import com.hendraanggrian.javapoet.dsl.TypeNameHandlerScope
import com.hendraanggrian.javapoet.dsl.TypeSpecHandler
import com.hendraanggrian.javapoet.dsl.TypeSpecHandlerScope
import com.hendraanggrian.javapoet.dsl.TypeVariableNameHandler
import com.hendraanggrian.javapoet.dsl.TypeVariableNameHandlerScope
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.lang.reflect.Type
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

/** Builds new class [TypeSpec] with name. */
fun classTypeSpecOf(type: String): TypeSpec = TypeSpec.classBuilder(type).build()

/** Builds new class [TypeSpec] from [ClassName]. */
fun classTypeSpecOf(type: ClassName): TypeSpec = TypeSpec.classBuilder(type).build()

/** Builds new interface [TypeSpec] from name. */
fun interfaceTypeSpecOf(type: String): TypeSpec = TypeSpec.interfaceBuilder(type).build()

/** Builds new interface [TypeSpec] from [ClassName]. */
fun interfaceTypeSpecOf(type: ClassName): TypeSpec = TypeSpec.interfaceBuilder(type).build()

/** Builds new enum [TypeSpec] from name. */
fun enumTypeSpecOf(type: String): TypeSpec = TypeSpec.enumBuilder(type).build()

/** Builds new enum [TypeSpec] from [ClassName]. */
fun enumTypeSpecOf(type: ClassName): TypeSpec = TypeSpec.enumBuilder(type).build()

/** Builds new anonymous [TypeSpec] from formatting. */
fun anonymousTypeSpecOf(format: String, vararg args: Any): TypeSpec =
    format.internalFormat(args) { s, array -> TypeSpec.anonymousClassBuilder(s, *array).build() }

/** Builds new anonymous [TypeSpec] from [CodeBlock]. */
fun anonymousTypeSpecOf(code: CodeBlock): TypeSpec = TypeSpec.anonymousClassBuilder(code).build()

/** Builds new annotation [TypeSpec] from name. */
fun annotationTypeSpecOf(type: String): TypeSpec = TypeSpec.annotationBuilder(type).build()

/** Builds new annotation [TypeSpec] from [ClassName]. */
fun annotationTypeSpecOf(type: ClassName): TypeSpec = TypeSpec.annotationBuilder(type).build()

/**
 * Builds new class [TypeSpec] from name,
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildClassTypeSpec(type: String, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.classBuilder(type)).apply(configuration).build()

/**
 * Builds new class [TypeSpec] from [ClassName],
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildClassTypeSpec(type: ClassName, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.classBuilder(type)).apply(configuration).build()

/**
 * Builds new interface [TypeSpec] from name,
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildInterfaceTypeSpec(type: String, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.interfaceBuilder(type)).apply(configuration).build()

/**
 * Builds new interface [TypeSpec] from [ClassName],
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildInterfaceTypeSpec(type: ClassName, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.interfaceBuilder(type)).apply(configuration).build()

/**
 * Builds new enum [TypeSpec] from name,
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildEnumTypeSpec(type: String, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.enumBuilder(type)).apply(configuration).build()

/**
 * Builds new enum [TypeSpec] from [ClassName],
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildEnumTypeSpec(type: ClassName, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.enumBuilder(type)).apply(configuration).build()

/**
 * Builds new anonymous [TypeSpec] from formatting,
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 * Not inlining this function since [internalFormat] is not inlined.
 */
fun buildAnonymousTypeSpec(format: String, vararg args: Any, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    format.internalFormat(args) { s, array ->
        TypeSpecBuilder(TypeSpec.anonymousClassBuilder(s, *array)).apply(configuration).build()
    }

/**
 * Builds new anonymous [TypeSpec] from [CodeBlock],
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildAnonymousTypeSpec(code: CodeBlock, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.anonymousClassBuilder(code)).apply(configuration).build()

/**
 * Builds new annotation [TypeSpec] from name,
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildAnnotationTypeSpec(type: String, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.annotationBuilder(type)).apply(configuration).build()

/**
 * Builds new annotation [TypeSpec] from [ClassName],
 * by populating newly created [TypeSpecBuilder] using provided [configuration].
 */
fun buildAnnotationTypeSpec(type: ClassName, configuration: TypeSpecBuilder.() -> Unit): TypeSpec =
    TypeSpecBuilder(TypeSpec.annotationBuilder(type)).apply(configuration).build()

/** Modify existing [TypeSpec.Builder] using provided [configuration]. */
fun TypeSpec.Builder.edit(configuration: TypeSpecBuilder.() -> Unit): TypeSpec.Builder =
    TypeSpecBuilder(this).apply(configuration).nativeBuilder

/**
 * Wrapper of [TypeSpec.Builder], providing DSL support as a replacement to Java builder.
 * @param nativeBuilder source builder.
 */
@SpecDslMarker
class TypeSpecBuilder internal constructor(val nativeBuilder: TypeSpec.Builder) {

    /** Enum constants of this type. */
    val enumConstants: MutableMap<String, TypeSpec> get() = nativeBuilder.enumConstants

    /** Modifiers of this type. */
    val modifiers: MutableList<Modifier> get() = nativeBuilder.modifiers

    /** Originating elements of this type. */
    val originatingElements: MutableList<Element> get() = nativeBuilder.originatingElements

    /** Always qualified names of this type. */
    val alwaysQualifiedNames: MutableSet<String> get() = nativeBuilder.alwaysQualifiedNames

    /** Javadoc of this type. */
    val javadoc: JavadocHandler = object : JavadocHandler() {
        override fun append(format: String, vararg args: Any): Unit =
            format.internalFormat(args) { s, array -> nativeBuilder.addJavadoc(s, *array) }

        override fun append(code: CodeBlock) {
            nativeBuilder.addJavadoc(code)
        }
    }

    /** Configures javadoc of this type. */
    fun javadoc(configuration: JavadocHandlerScope.() -> Unit): Unit =
        JavadocHandlerScope(javadoc).configuration()

    /** Annotations of this type. */
    val annotations: AnnotationSpecHandler = AnnotationSpecHandler(nativeBuilder.annotations)

    /** Configures annotations of this type. */
    fun annotations(configuration: AnnotationSpecHandlerScope.() -> Unit): Unit =
        AnnotationSpecHandlerScope(annotations).configuration()

    /** Add type modifiers. */
    fun addModifiers(vararg modifiers: Modifier) {
        nativeBuilder.addModifiers(*modifiers)
    }

    /** Type variables of this type. */
    val typeVariables: TypeVariableNameHandler = TypeVariableNameHandler(nativeBuilder.typeVariables)

    /** Configures type variables of this method. */
    fun typeVariables(configuration: TypeVariableNameHandlerScope.() -> Unit): Unit =
        TypeVariableNameHandlerScope(typeVariables).configuration()

    /** Set superclass to type. */
    var superclass: TypeName
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            nativeBuilder.superclass(value)
        }

    /** Set superclass to [type]. */
    fun superclass(type: Type) {
        nativeBuilder.superclass(type)
    }

    /** Set superclass to [type]. */
    fun superclass(type: KClass<*>) {
        nativeBuilder.superclass(type.java)
    }

    /** Set superclass to [T]. */
    inline fun <reified T> superclass(): Unit = superclass(T::class)

    /** Super interfaces of this type. */
    val superinterfaces: TypeNameHandler = TypeNameHandler(nativeBuilder.superinterfaces)

    /** Configures super interfaces of this type. */
    fun superinterfaces(configuration: TypeNameHandlerScope.() -> Unit): Unit =
        TypeNameHandlerScope(superinterfaces).configuration()

    /** Add enum constant with name. */
    fun addEnumConstant(name: String) {
        nativeBuilder.addEnumConstant(name)
    }

    /** Add enum constant with name and specified [TypeSpec]. */
    fun addEnumConstant(name: String, spec: TypeSpec) {
        nativeBuilder.addEnumConstant(name, spec)
    }

    /** Fields of this type. */
    val fields: FieldSpecHandler = FieldSpecHandler(nativeBuilder.fieldSpecs)

    /** Configures fields of this type. */
    fun fields(configuration: FieldSpecHandlerScope.() -> Unit): Unit =
        FieldSpecHandlerScope(fields).configuration()

    /** Add static block containing [code]. */
    fun addStaticBlock(code: CodeBlock) {
        nativeBuilder.addStaticBlock(code)
    }

    /** Add static block containing code with custom initialization [configuration]. */
    fun addStaticBlock(configuration: CodeBlockBuilder.() -> Unit): Unit =
        addStaticBlock(buildCodeBlock(configuration))

    /** Add initializer block containing [code]. */
    fun addInitializerBlock(code: CodeBlock) {
        nativeBuilder.addInitializerBlock(code)
    }

    /** Add initializer block containing code with custom initialization [configuration]. */
    fun addInitializerBlock(configuration: CodeBlockBuilder.() -> Unit): Unit =
        addInitializerBlock(buildCodeBlock(configuration))

    /** Methods of this type. */
    val methods: MethodSpecHandler = MethodSpecHandler(nativeBuilder.methodSpecs)

    /** Configures methods of this type. */
    fun methods(configuration: MethodSpecHandlerScope.() -> Unit): Unit =
        MethodSpecHandlerScope(methods).configuration()

    /** Types of this type. */
    val types: TypeSpecHandler = TypeSpecHandler(nativeBuilder.typeSpecs)

    /** Configures types of this type. */
    fun types(configuration: TypeSpecHandlerScope.() -> Unit): Unit =
        TypeSpecHandlerScope(types).configuration()

    /** Add originating element. */
    fun addOriginatingElement(originatingElement: Element) {
        nativeBuilder.addOriginatingElement(originatingElement)
    }

    fun alwaysQualify(vararg simpleNames: String) {
        nativeBuilder.alwaysQualify(*simpleNames)
    }

    fun avoidClashesWithNestedClasses(typeElement: TypeElement) {
        nativeBuilder.avoidClashesWithNestedClasses(typeElement)
    }

    fun avoidClashesWithNestedClasses(type: Class<*>) {
        nativeBuilder.avoidClashesWithNestedClasses(type)
    }

    fun avoidClashesWithNestedClasses(type: KClass<*>) {
        nativeBuilder.avoidClashesWithNestedClasses(type.java)
    }

    inline fun <reified T> avoidClashesWithNestedClasses(): Unit = avoidClashesWithNestedClasses(T::class)

    /** Returns native spec. */
    fun build(): TypeSpec = nativeBuilder.build()
}
