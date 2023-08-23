package org.wordpress.android.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test
import org.wordpress.android.lint.Utils.nonNullClass
import org.wordpress.android.lint.Utils.nullableClass


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
    fun `it should not inform when @NotNull annotation is present on field`() {
        lint().files(
                nonNullClass,
                LintDetectorTest.java("""
            package test;
            
            import androidx.annotation.NonNull;
            
            class ExampleClass {
              @NonNull String mExampleField = "example";
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION)
                .run()
                .expectClean()
    }

    @Test
    fun `it should not inform when @Nullable annotation is present on field`() {
        lint().files(
                nullableClass,
                LintDetectorTest.java("""
            package test;
            
            import androidx.annotation.Nullable;
            
            class ExampleClass {
              @Nullable String mExampleField = "example";
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION)
                .run()
                .expectClean()
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
    fun `it should not inform when @NonNull annotation is present on a method return type`() {
        lint().files(
                nonNullClass,
                LintDetectorTest.java("""
            package test;
            
            import androidx.annotation.NonNull;

            class ExampleClass {
              @NonNull String getMessage() {
                return "example";
              }
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_RETURN_TYPE_ANNOTATION)
                .run()
                .expectClean()
    }

    @Test
    fun `it should not inform when @Nullable annotation is present on a method return type`() {
        lint().files(
                nullableClass,
                LintDetectorTest.java("""
            package test;

            import androidx.annotation.Nullable;
            
            class ExampleClass {
              @Nullable String getMessage() {
                return "example";
              }
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_RETURN_TYPE_ANNOTATION)
                .run()
                .expectClean()
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
    fun `it should not inform when @NonNull annotation is present on a method parameter`() {
        lint().files(
                nonNullClass,
                LintDetectorTest.java("""
            package test;

            import androidx.annotation.NonNull;
            
            class ExampleClass {
              String getMessage(@NonNull String name) {
                return name + " example";
              }
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_PARAMETER_ANNOTATION)
                .run()
                .expectClean()
    }

    @Test
    fun `it should not inform when @Nullable annotation is present on a method parameter`() {
        lint().files(
                nullableClass,
                LintDetectorTest.java("""
            package test;

            import androidx.annotation.Nullable;
            
            class ExampleClass {
              String getMessage(@Nullable String name) {
                return name + " example";
              }
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_METHOD_PARAMETER_ANNOTATION)
                .run()
                .expectClean()
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
    fun `it should not inform when @NonNull annotation is present on a construction parameter`() {
        lint().files(
                nonNullClass,
                LintDetectorTest.java("""
            package test;
            
            import androidx.annotation.NonNull;

            class ExampleClass {
              ExampleClass(@NonNull String name) {}
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION)
                .run()
                .expectClean()
    }
    @Test
    fun `it should inform when @Nullable annotation is present on a construction parameter`() {
        lint().files(
                nullableClass,
                LintDetectorTest.java("""
            package test;
            
            import androidx.annotation.Nullable;

            class ExampleClass {
              ExampleClass(@Nullable String name) {}
            }
        """).indented())
                .issues(MissingNullAnnotationDetector.MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION)
                .run()
                .expectClean()
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
