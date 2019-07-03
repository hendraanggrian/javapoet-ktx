package com.hendraanggrian.javapoet.dsl

import com.hendraanggrian.javapoet.CodeBlockBuilder
import com.hendraanggrian.javapoet.invoke
import com.squareup.javapoet.CodeBlock

/** A [MemberContainer] is responsible for managing a set of member instances. */
abstract class MemberContainer internal constructor() {

    /** Add member from [name] and code block to this container. */
    abstract fun add(name: String, format: String, vararg args: Any)

    /** Add member from [name] and [block] to this container, returning the member added. */
    abstract fun add(name: String, block: CodeBlock): CodeBlock

    /** Add member with [name] and custom initialization [builder], returning the member added. */
    inline fun add(name: String, builder: CodeBlockBuilder.() -> Unit): CodeBlock =
        add(name, CodeBlock.builder()(builder))

    /** Convenient method to add member with operator function. */
    operator fun set(name: String, format: String) {
        add(name, format)
    }

    /** Convenient method to add member with operator function. */
    operator fun set(name: String, block: CodeBlock) {
        add(name, block)
    }

    /** Configure this container with DSL. */
    inline operator fun invoke(configuration: MemberContainerScope.() -> Unit) =
        MemberContainerScope(this).configuration()
}

/** Receiver for the `members` block providing an extended set of operators for the configuration. */
class MemberContainerScope @PublishedApi internal constructor(private val container: MemberContainer) :
    MemberContainer() {

    override fun add(name: String, format: String, vararg args: Any) = container.add(name, format, *args)
    override fun add(name: String, block: CodeBlock): CodeBlock = container.add(name, block)

    /** Convenient method to add member with receiver type. */
    inline operator fun String.invoke(builder: CodeBlockBuilder.() -> Unit): CodeBlock =
        add(this, builder)
}