package com.hendraanggrian.javapoet

import com.hendraanggrian.javapoet.dsl.TypeCollection
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import kotlin.reflect.KClass

/**
 * Builds new [JavaFile] by populating newly created [JavaFileBuilder] using provided [builderAction]
 * and then building it.
 */
inline fun buildJavaFile(packageName: String, builderAction: JavaFileBuilder.() -> Unit): JavaFile =
    JavaFileBuilder(packageName).apply(builderAction).build()

/** Wrapper of [JavaFile.Builder], providing DSL support as a replacement to Java builder. */
@JavapoetDslMarker
class JavaFileBuilder @PublishedApi internal constructor(private val packageName: String) : TypeCollection() {

    private var typeSpec: TypeSpec? = null
    private var comments: MutableMap<String, Array<Any>>? = null
    private var staticImports: MutableMap<Any, Array<String>>? = null
    private var isSkipJavaLangImports: Boolean? = null
    private var indentString: String? = null

    override fun add(spec: TypeSpec): TypeSpec = spec.also {
        check(typeSpec == null) { "Java file may only have 1 type" }
        typeSpec = it
    }

    /** Add a comment to this file. */
    fun addComment(format: String, vararg args: Any) {
        if (comments == null) {
            comments = mutableMapOf()
        }
        comments!![format] = arrayOf(*args)
    }

    /** Set a comment to this file, cancelling all changes made with [addComment]. */
    var comment: String
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            comments = mutableMapOf(value to emptyArray())
        }

    /** Add static imports to this file. */
    fun addStaticImport(constant: Enum<*>) {
        if (staticImports == null) {
            staticImports = mutableMapOf()
        }
        staticImports!![constant] = emptyArray()
    }

    /** Add static imports to this file. */
    fun addStaticImport(type: ClassName, vararg names: String) {
        if (staticImports == null) {
            staticImports = mutableMapOf()
        }
        staticImports!![type] = arrayOf(*names)
    }

    /** Add static imports to this file. */
    fun addStaticImport(type: KClass<*>, vararg names: String) {
        if (staticImports == null) {
            staticImports = mutableMapOf()
        }
        staticImports!![type] = arrayOf(*names)
    }

    /** Add static imports to this file. */
    inline fun <reified T> addStaticImport(vararg names: String) =
        addStaticImport(T::class, *names)

    var skipJavaLangImports: Boolean
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            isSkipJavaLangImports = value
        }

    var indent: String
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            indentString = value
        }

    inline var indentCount: Int
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            indent = buildString { repeat(value) { append(' ') } }
        }

    fun build(): JavaFile =
        JavaFile.builder(packageName, checkNotNull(typeSpec) { "A main type must be initialized" })
            .apply {
                comments?.forEach { (format, args) -> addFileComment(format, *args) }
                staticImports?.forEach { (type, names) ->
                    when (type) {
                        is Enum<*> -> addStaticImport(type)
                        is ClassName -> addStaticImport(type, *names)
                        is KClass<*> -> addStaticImport(type.java, *names)
                    }
                }
                isSkipJavaLangImports?.let { skipJavaLangImports(it) }
                indentString?.let { indent(it) }
            }
            .build()
}
