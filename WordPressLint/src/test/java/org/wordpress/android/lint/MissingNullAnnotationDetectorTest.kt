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
                .allowCompilationErrors()
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
    fun `it ignores injections`() {
        lint().files(LintDetectorTest.java("""
            package test;

            class ExampleClass {
              @Inject String mExampleField = "example";
            }
        """).indented())
                .allowCompilationErrors()
                .issues(MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION)
                .run()
                .expectClean()
    }
}
