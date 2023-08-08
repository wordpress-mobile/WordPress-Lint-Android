package org.wordpress.android.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test
import org.wordpress.android.lint.WordPressAndroidImportInViewModelDetector.Companion.ISSUE_ANDROID_IMPORT_IN_VIEWMODEL

class WordPressAndroidApiInViewModelDetectorTest {
    @Test
    fun `when ViewModel contains Android import then test should fail`() {
        lint().files(kotlin("""
            package test
            import android.widget.TextView
            import androidx.lifecycle.ViewModel
            import org.wordpress.android.fluxc.store.SiteStore

            class TestViewModel : ViewModel() {
              lateinit var textView: TextView
             }
        """).indented())
                .allowCompilationErrors()
                .issues(ISSUE_ANDROID_IMPORT_IN_VIEWMODEL)
                .run()
                .expect("src/test/TestViewModel.kt:2: Error: ViewModels shouldn't " +
                        "know anything about the Android framework classes. This improves " +
                        "testability & modularity. [AndroidImportsInViewModel]\n" +
                        "import android.widget.TextView\n" +
                        "       ~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "1 errors, 0 warnings")
    }

    @Test
    fun `when ViewModel contains ALLOWED_ANDROID_IMPORTS then test should pass`() {
        lint().files(kotlin("""
            package test
            import org.wordpress.android.fluxc.store.SiteStore
            import android.R.color.black
            import androidx.lifecycle.ViewModel

            class TestViewModel : ViewModel()
        """).indented())
                .allowCompilationErrors()
                .issues(ISSUE_ANDROID_IMPORT_IN_VIEWMODEL)
                .run()
                .expectClean()
    }

    @Test
    fun `ViewModel class should not trigger the lint check`() {
        lint().files(kotlin("""
            package androidx.lifecycle

            class ViewModel
        """).indented())
                .allowCompilationErrors()
                .issues(ISSUE_ANDROID_IMPORT_IN_VIEWMODEL)
                .run()
                .expectClean()
    }

    @Test
    fun `A normal class shouldn't trigger the check`() {
        lint().files(kotlin("""
            package test

            class TestClass
        """).indented())
                .issues(ISSUE_ANDROID_IMPORT_IN_VIEWMODEL)
                .run()
                .expectClean()
    }
}
