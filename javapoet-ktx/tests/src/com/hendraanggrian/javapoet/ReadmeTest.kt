package com.hendraanggrian.javapoet

import com.hendraanggrian.javapoet.dsl.MethodContainerScope
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import java.util.Collections
import java.util.Date
import javax.lang.model.element.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals

/** From the original javapoet readme. */
class ReadmeTest {

    @Test
    fun example() {
        assertEquals(
            """
                package com.example.helloworld;

                import java.lang.String;
                import java.lang.System;

                public final class HelloWorld {
                  public static void main(String[] args) {
                    System.out.println("Hello, JavaPoet!");
                  }
                }

            """.trimIndent(),
            buildJavaFile("com.example.helloworld") {
                addClass("HelloWorld") {
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    methods.add("main") {
                        addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        returns = TypeName.VOID
                        parameters.add<Array<String>>("args")
                        appendln("%T.out.println(%S)", System::class, "Hello, JavaPoet!")
                    }
                }
            }.toString()
        )
    }

    @Test
    fun `code&ControlFlow`() {
        val expected =
            """
                void main() {
                  int total = 0;
                  for (int i = 0; i < 10; i++) {
                    total += i;
                  }
                }
            
            """.trimIndent()
        assertEquals(
            expected,
            buildMethod("main") {
                returns = TypeName.VOID
                append(
                    """
                        int total = 0;
                        for (int i = 0; i < 10; i++) {
                          total += i;
                        }
                        
                    """.trimIndent()
                )
            }.toString()
        )
        assertEquals(
            expected,
            buildMethod("main") {
                returns = TypeName.VOID
                appendln("int total = 0")
                beginControlFlow("for (int i = 0; i < 10; i++)")
                appendln("total += i")
                endControlFlow()
            }.toString()
        )
        assertEquals(
            """
                int multiply10to20() {
                  int result = 1;
                  for (int i = 10; i < 20; i++) {
                    result = result * i;
                  }
                  return result;
                }

            """.trimIndent(),
            buildMethod("multiply10to20") {
                returns = TypeName.INT
                appendln("int result = 1")
                beginControlFlow("for (int i = 10; i < 20; i++)")
                appendln("result = result * i")
                endControlFlow()
                appendln("return result")
            }.toString()
        )
        assertEquals(
            """
                void main() {
                  long now = java.lang.System.currentTimeMillis();
                  if (java.lang.System.currentTimeMillis() < now) {
                    java.lang.System.out.println("Time travelling, woo hoo!");
                  } else if (java.lang.System.currentTimeMillis() == now) {
                    java.lang.System.out.println("Time stood still!");
                  } else {
                    java.lang.System.out.println("Ok, time still moving forward");
                  }
                }

            """.trimIndent(),
            buildMethod("main") {
                appendln("long now = %T.currentTimeMillis()", System::class)
                beginControlFlow("if (%T.currentTimeMillis() < now)", System::class)
                appendln("%T.out.println(%S)", System::class, "Time travelling, woo hoo!")
                nextControlFlow("else if (%T.currentTimeMillis() == now)", System::class)
                appendln("%T.out.println(%S)", System::class, "Time stood still!")
                nextControlFlow("else")
                appendln("%T.out.println(%S)", System::class, "Ok, time still moving forward")
                endControlFlow()
            }.toString()
        )
        assertEquals(
            """
                void main() {
                  try {
                    throw new Exception("Failed");
                  } catch (java.lang.Exception e) {
                    throw new java.lang.RuntimeException(e);
                  }
                }
                
            """.trimIndent(),
            buildMethod("main") {
                beginControlFlow("try")
                appendln("throw new Exception(%S)", "Failed")
                nextControlFlow("catch (%T e)", Exception::class)
                appendln("throw new %T(e)", RuntimeException::class)
                endControlFlow()
            }.toString()
        )
    }

    @Test
    fun `$SForStrings`() {
        assertEquals(
            """
                public final class HelloWorld {
                  java.lang.String slimShady() {
                    return "slimShady";
                  }

                  java.lang.String eminem() {
                    return "eminem";
                  }

                  java.lang.String marshallMathers() {
                    return "marshallMathers";
                  }
                }

            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                methods {
                    nameMethod("slimShady")
                    nameMethod("eminem")
                    nameMethod("marshallMathers")
                }
            }.toString()
        )
    }

    @Test
    fun `$TForTypes`() {
        assertEquals(
            """
                public final class HelloWorld {
                  java.util.Date today() {
                    return new java.util.Date();
                  }
                }

            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                methods.add("today") {
                    returns<Date>()
                    appendln("return new %T()", Date::class)
                }
            }.toString()
        )
        assertEquals(
            """
                public final class HelloWorld {
                  com.mattel.Hoverboard tomorrow() {
                    return new com.mattel.Hoverboard();
                  }
                }

            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                methods.add("tomorrow") {
                    val hoverboard = "com.mattel".toClassName("Hoverboard")
                    returns = hoverboard
                    appendln("return new %T()", hoverboard)
                }
            }.toString()
        )
        assertEquals(
            """
                public final class HelloWorld {
                  java.util.List<com.mattel.Hoverboard> beyond() {
                    java.util.List<com.mattel.Hoverboard> result = new java.util.ArrayList<>();
                    result.add(new com.mattel.Hoverboard());
                    result.add(new com.mattel.Hoverboard());
                    result.add(new com.mattel.Hoverboard());
                    return result;
                  }
                }

            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                methods.add("beyond") {
                    val hoverboard = "com.mattel".toClassName("Hoverboard")
                    val list = "java.util".toClassName("List")
                    val arrayList = "java.util".toClassName("ArrayList")
                    val listOfHoverboards = list.toParameterizedTypeName(hoverboard)
                    returns = listOfHoverboards
                    appendln("%T result = new %T<>()", listOfHoverboards, arrayList)
                    appendln("result.add(new %T())", hoverboard)
                    appendln("result.add(new %T())", hoverboard)
                    appendln("result.add(new %T())", hoverboard)
                    appendln("return result")
                }
            }.toString()
        )
        assertEquals(
            """
                package com.example.helloworld;

                import static com.mattel.Hoverboard.Boards.*;
                import static com.mattel.Hoverboard.createNimbus;
                import static java.util.Collections.*;

                import com.mattel.Hoverboard;
                import java.util.ArrayList;
                import java.util.List;

                public final class HelloWorld {
                  List<Hoverboard> beyond() {
                    List<Hoverboard> result = new ArrayList<>();
                    result.add(createNimbus(2000));
                    result.add(createNimbus("2001"));
                    result.add(createNimbus(THUNDERBOLT));
                    sort(result);
                    return result.isEmpty() ? emptyList() : result;
                  }
                }

            """.trimIndent(),
            buildJavaFile("com.example.helloworld") {
                val hoverboard = "com.mattel".toClassName("Hoverboard")
                val namedBoards = "com.mattel".toClassName("Hoverboard", "Boards")
                addStaticImport(hoverboard, "createNimbus")
                addStaticImport(namedBoards, "*")
                addStaticImport(Collections::class, "*")
                addClass("HelloWorld") {
                    addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    methods.add("beyond") {
                        val list = "java.util".toClassName("List")
                        val arrayList = "java.util".toClassName("ArrayList")
                        val listOfHoverboards = list.toParameterizedTypeName(hoverboard)
                        returns = listOfHoverboards
                        appendln("%T result = new %T<>()", listOfHoverboards, arrayList)
                        appendln("result.add(%T.createNimbus(2000))", hoverboard)
                        appendln("result.add(%T.createNimbus(\"2001\"))", hoverboard)
                        appendln("result.add(%T.createNimbus(%T.THUNDERBOLT))", hoverboard, namedBoards)
                        appendln("%T.sort(result)", Collections::class)
                        appendln("return result.isEmpty() ? %T.emptyList() : result", Collections::class)
                    }
                }
            }.toString()
        )
    }

    @Test
    fun `$NForNames`() {
        val hexDigit = buildMethod("hexDigit") {
            addModifiers(Modifier.PUBLIC)
            parameters.add(ClassName.INT, "i")
            returns = ClassName.CHAR
            appendln("return (char) (i < 10 ? i + '0' : i - 10 + 'a')")
        }
        val byteToHex = buildMethod("byteToHex") {
            addModifiers(Modifier.PUBLIC)
            parameters.add(ClassName.INT, "b")
            returns<String>()
            appendln("char[] result = new char[2]")
            appendln("result[0] = %N((b >>> 4) & 0xf)", hexDigit)
            appendln("result[1] = %N(b & 0xf)", hexDigit)
            appendln("return new String(result)")
        }
        assertEquals(
            """
                public java.lang.String byteToHex(int b) {
                  char[] result = new char[2];
                  result[0] = hexDigit((b >>> 4) & 0xf);
                  result[1] = hexDigit(b & 0xf);
                  return new String(result);
                }

                public char hexDigit(int i) {
                  return (char) (i < 10 ? i + '0' : i - 10 + 'a');
                }

            """.trimIndent(),
            "$byteToHex\n$hexDigit"
        )
    }

    @Test
    fun codeBlockFormatStrings() {
        assertEquals(
            CodeBlock.of("I ate \$L \$L", 3, "tacos"),
            "I ate %L %L".toCode(3, "tacos")
        )
        assertEquals(
            CodeBlock.of("I ate \$2L \$1L", "tacos", 3),
            "I ate %2L %1L".toCode("tacos", 3)
        )
    }

    @Test
    fun methods() {
        assertEquals(
            """
                public abstract class HelloWorld {
                  protected abstract void flux();
                }
                
            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                methods.add("flux") {
                    addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                }
            }.toString()
        )
    }

    @Test
    fun constructors() {
        assertEquals(
            """
                public class HelloWorld {
                  private final java.lang.String greeting;

                  public HelloWorld(java.lang.String greeting) {
                    this.greeting = greeting;
                  }
                }
                
            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC)
                fields.add<String>("greeting", Modifier.PRIVATE, Modifier.FINAL)
                methods.addConstructor {
                    addModifiers(Modifier.PUBLIC)
                    parameters.add<String>("greeting")
                    appendln("this.%N = %N", "greeting", "greeting")
                }
            }.toString()
        )
    }

    @Test
    fun parameters() {
        assertEquals(
            """
                void welcomeOverlords(final java.lang.String android, final java.lang.String robot) {
                }

            """.trimIndent(),
            buildMethod("welcomeOverlords") {
                parameters {
                    add<String>("android", Modifier.FINAL)
                    add<String>("robot", Modifier.FINAL)
                }
            }.toString()
        )
    }

    @Test
    fun fields() {
        assertEquals(
            """
                public class HelloWorld {
                  private final java.lang.String android;

                  private final java.lang.String robot;
                }
                
            """.trimIndent(),
            buildClassType("HelloWorld") {
                addModifiers(Modifier.PUBLIC)
                fields {
                    add<String>("android", Modifier.PRIVATE, Modifier.FINAL)
                    add<String>("robot", Modifier.PRIVATE, Modifier.FINAL)
                }
            }.toString()
        )
        assertEquals(
            "private final java.lang.String android = \"Lollipop v.\" + 5.0;\n",
            buildField<String>("android", Modifier.PRIVATE, Modifier.FINAL) {
                initializer = "\"Lollipop v.\" + 5.0"
            }.toString()
        )
    }

    @Test
    fun interfaces() {
        assertEquals(
            """
                public interface HelloWorld {
                  java.lang.String ONLY_THING_THAT_IS_CONSTANT = "change";

                  void beep();
                }
                
            """.trimIndent(),
            buildInterfaceType("HelloWorld") {
                addModifiers(Modifier.PUBLIC)
                fields.add<String>("ONLY_THING_THAT_IS_CONSTANT", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL) {
                    initializer("%S", "change")
                }
                methods.add("beep") {
                    addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                }
            }.toString()
        )
    }

    @Test
    fun enums() {
        assertEquals(
            """
                public enum Roshambo {
                  ROCK,

                  SCISSORS,

                  PAPER
                }
                
            """.trimIndent(),
            buildEnumType("Roshambo") {
                addModifiers(Modifier.PUBLIC)
                addEnumConstant("ROCK")
                addEnumConstant("SCISSORS")
                addEnumConstant("PAPER")
            }.toString()
        )
        assertEquals(
            """
                public enum Roshambo {
                  ROCK("fist") {
                    @java.lang.Override
                    public java.lang.String toString() {
                      return "avalanche!";
                    }
                  },

                  SCISSORS("peace"),

                  PAPER("flat");

                  private final java.lang.String handsign;

                  Roshambo(java.lang.String handsign) {
                    this.handsign = handsign;
                  }
                }
                
            """.trimIndent(),
            buildEnumType("Roshambo") {
                addModifiers(Modifier.PUBLIC)
                addEnumConstant("ROCK", buildAnonymousType("%S", "fist") {
                    methods.add("toString") {
                        annotations.add<Override>()
                        addModifiers(Modifier.PUBLIC)
                        appendln("return %S", "avalanche!")
                        returns<String>()
                    }
                })
                addEnumConstant("SCISSORS", "%S".toAnonymousType("peace"))
                addEnumConstant("PAPER", "%S".toAnonymousType("flat"))
                fields.add<String>("handsign", Modifier.PRIVATE, Modifier.FINAL)
                methods.addConstructor {
                    parameters.add<String>("handsign")
                    appendln("this.%N = %N", "handsign", "handsign")
                }
            }.toString()
        )
    }

    @Test
    fun anonymousInnerClasses() {
        lateinit var sortByLength: MethodSpec
        buildClassType("HelloWorld") {
            sortByLength = methods.add("sortByLength") {
                parameters.add(List::class.toParameterizedTypeName(String::class), "strings")
                appendln("%T.sort(%N, %L)", Collections::class, "strings", buildAnonymousType("") {
                    addSuperInterface(Comparator::class.toParameterizedTypeName(String::class))
                    methods.add("compare") {
                        annotations.add<Override>()
                        addModifiers(Modifier.PUBLIC)
                        parameters {
                            add<String>("a")
                            add<String>("b")
                        }
                        returns = TypeName.INT
                        appendln("return %N.length() - %N.length()", "a", "b")
                    }
                })
            }
        }
        assertEquals(
            """
                void sortByLength(java.util.List<java.lang.String> strings) {
                  java.util.Collections.sort(strings, new java.util.Comparator<java.lang.String>() {
                    @java.lang.Override
                    public int compare(java.lang.String a, java.lang.String b) {
                      return a.length() - b.length();
                    }
                  });
                }
                
            """.trimIndent(),
            sortByLength.toString()
        )
    }

    @Test
    fun annotations() {
        assertEquals(
            """
                @java.lang.Override
                public java.lang.String toString() {
                  return "Hoverboard";
                }
                
            """.trimIndent(),
            buildMethod("toString") {
                annotations.add<Override>()
                returns<String>()
                addModifiers(Modifier.PUBLIC)
                appendln("return %S", "Hoverboard")
            }.toString()
        )
        assertEquals(
            """
                @com.hendraanggrian.javapoet.ReadmeTest.Headers(
                    accept = "application/json; charset=utf-8",
                    userAgent = "Square Cash"
                )
                public abstract com.hendraanggrian.javapoet.ReadmeTest.LogReceipt recordEvent(
                    com.hendraanggrian.javapoet.ReadmeTest.LogRecord logRecord);
                
            """.trimIndent(),
            buildMethod("recordEvent") {
                addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                annotations.add<Headers> {
                    addMember("accept", "%S", "application/json; charset=utf-8")
                    addMember("userAgent", "%S", "Square Cash")
                }
                parameters.add<LogRecord>("logRecord")
                returns<LogReceipt>()
            }.toString()
        )
        assertEquals(
            """
                @com.hendraanggrian.javapoet.ReadmeTest.HeaderList({
                    @com.hendraanggrian.javapoet.ReadmeTest.Header(name = "Accept", value = "application/json; charset=utf-8"),
                    @com.hendraanggrian.javapoet.ReadmeTest.Header(name = "User-Agent", value = "Square Cash")
                })
                public abstract com.hendraanggrian.javapoet.ReadmeTest.LogReceipt recordEvent(
                    com.hendraanggrian.javapoet.ReadmeTest.LogRecord logRecord);
                
            """.trimIndent(),
            buildMethod("recordEvent") {
                addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                annotations.add<HeaderList> {
                    addMember("value", "%L", buildAnnotation<Header> {
                        addMember("name", "%S", "Accept")
                        addMember("value", "%S", "application/json; charset=utf-8")
                    })
                    addMember("value", "%L", buildAnnotation<Header> {
                        addMember("name", "%S", "User-Agent")
                        addMember("value", "%S", "Square Cash")
                    })
                }
                parameters.add<LogRecord>("logRecord")
                returns<LogReceipt>()
            }.toString()
        )
    }

    @Test
    fun javadoc() {
        assertEquals(
            """
                /**
                 * Hides {@code message} from the caller's history. Other
                 * participants in the conversation will continue to see the
                 * message in their own history unless they also delete it.
                 *
                 * <p>Use {@link #delete(com.hendraanggrian.javapoet.ReadmeTest.Conversation)} to delete the entire
                 * conversation for all participants.
                 */
                public abstract void dismiss(com.hendraanggrian.javapoet.ReadmeTest.Message message);

            """.trimIndent(),
            buildMethod("dismiss") {
                javadoc {
                    appendln(
                        "Hides {@code message} from the caller's history. Other\n"
                            + "participants in the conversation will continue to see the\n"
                            + "message in their own history unless they also delete it.\n"
                    )
                    append(
                        "<p>Use {@link #delete(%T)} to delete the entire\n"
                            + "conversation for all participants.\n", Conversation::class
                    )
                }
                addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                parameters.add<Message>("message")
            }.toString()
        )
    }

    private fun MethodContainerScope.nameMethod(name: String) {
        name {
            returns<String>()
            appendln("return %S", name)
        }
    }

    private class Headers
    private class Header
    private class HeaderList
    private class LogRecord
    private class LogReceipt

    private class Message
    private class Conversation
}