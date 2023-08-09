package org.wordpress.android.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test


class MissingNullAnnotationDetectorTest {
    @Test
    fun `it should inform when null annotation is missing on field`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              String mExampleField = "example";
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION)
                .run()
                .expect("""
                    src/test/ExampleClass.java:4: Information: Missing null annotation [MissingNullAnnotationOnField]
                      String mExampleField = "example";
                      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    0 errors, 0 warnings
                            """
                        .trimIndent()
                )

    }
    @Test
    fun `it ignores injected fields`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              @Inject String mExampleField = "example";
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION)
                .run()
                .expectClean()
    }

    @Test
    fun `it should inform when null annotation is missing on a method return type`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              String getMessage() {
                return "example";
              }
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_RETURN_TYPE_ANNOTATION)
                .run()
                .expect("""
                    src/test/ExampleClass.java:4: Information: Missing null annotation [MissingNullAnnotationOnMethodReturnType]
                      String getMessage() {
                             ~~~~~~~~~~
                    0 errors, 0 warnings
                            """
                        .trimIndent()
                )
    }

    @Test
    fun `it should inform when null annotation is missing on a method parameter`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              String getMessage(String name) {
                return name + " example";
              }
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_PARAMETER_ANNOTATION)
                .run()
                .expect("""
                    src/test/ExampleClass.java:4: Information: Missing null annotation [MissingNullAnnotationOnMethodParameter]
                      String getMessage(String name) {
                                        ~~~~~~~~~~~
                    0 errors, 0 warnings
                            """
                        .trimIndent()
                )
    }
    @Test
    fun `it should inform when null annotation is missing on a construction parameter`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              ExampleClass(String name) {}
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION)
                .run()
                .expect("""
                    src/test/ExampleClass.java:4: Information: Missing null annotation [MissingNullAnnotationOnConstructorParameter]
                      ExampleClass(String name) {}
                                   ~~~~~~~~~~~
                    0 errors, 0 warnings
                            """
                        .trimIndent()
                )
    }
    @Test
    fun `it ignores injected constructors`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              @Inject ExampleClass(String name) {}
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_PARAMETER_ANNOTATION)
                .run()
                .expectClean()
    }
}
