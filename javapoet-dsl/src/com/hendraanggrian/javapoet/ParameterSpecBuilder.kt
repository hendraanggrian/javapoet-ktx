package com.hendraanggrian.javapoet

import com.hendraanggrian.javapoet.internal.Annotable
import com.hendraanggrian.javapoet.internal.Modifierable
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import java.lang.reflect.Type
import javax.lang.model.element.Modifier

/** Returns a parameter with custom initialization block. */
fun buildParameterSpec(
    type: TypeName,
    name: String,
    builder: (ParameterSpecBuilder.() -> Unit)? = null
): ParameterSpec = ParameterSpecBuilder(ParameterSpec.builder(type, name))
    .also { builder?.invoke(it) }
    .build()

/** Returns a parameter with custom initialization block. */
fun buildParameterSpec(
    type: Type,
    name: String,
    builder: (ParameterSpecBuilder.() -> Unit)? = null
): ParameterSpec = ParameterSpecBuilder(ParameterSpec.builder(type, name))
    .also { builder?.invoke(it) }
    .build()

class ParameterSpecBuilder(@PublishedApi internal val nativeBuilder: ParameterSpec.Builder) :
    Annotable,
    Modifierable {

    override fun annotation(type: ClassName, builder: (AnnotationSpecBuilder.() -> Unit)?) {
        nativeBuilder.addAnnotation(buildAnnotationSpec(type, builder))
    }

    override fun annotation(type: Class<*>, builder: (AnnotationSpecBuilder.() -> Unit)?) {
        nativeBuilder.addAnnotation(buildAnnotationSpec(type, builder))
    }

    inline fun <reified T> annotation(noinline builder: (AnnotationSpecBuilder.() -> Unit)? = null) =
        annotation(T::class.java, builder)

    override var modifiers: List<Modifier>
        @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
        set(value) {
            nativeBuilder.addModifiers(*value.toTypedArray())
        }

    fun build(): ParameterSpec = nativeBuilder.build()
}