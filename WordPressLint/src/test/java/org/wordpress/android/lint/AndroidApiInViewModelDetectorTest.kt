package org.wordpress.android.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test
import org.wordpress.android.lint.AndroidApiInViewModelDetector.Companion.ISSUE_ANDROID_API_IN_VIEWMODEL

class AndroidApiInViewModelDetectorTest {
    @Test
    fun `when ViewModel contains Android import then test should fail`() {
        lint().files(
                LintDetectorTest.kotlin(
                        """
            package test
            import android.widget.TextView
            import androidx.lifecycle.ViewModel
            import org.wordpress.android.fluxc.store.SiteStore

            class TestViewModel : ViewModel() {
              lateinit var textView: TextView
             }
        """
                ).indented())
                .issues(ISSUE_ANDROID_API_IN_VIEWMODEL)
                .run()
                .expectErrorCount(1)
    }

    @Test
    fun `when ViewModel contains allowed Android R import then test should pass`() {
        lint().files(
                LintDetectorTest.kotlin(
                        """
            package test
            import org.wordpress.android.fluxc.store.SiteStore
            import android.R.color.black
            import androidx.lifecycle.ViewModel

            class TestViewModel : ViewModel()
        """
                ).indented())
                .issues(ISSUE_ANDROID_API_IN_VIEWMODEL)
                .run()
                .expectClean()
    }

    @Test
    fun `ViewModel class should not trigger the lint check`() {
        lint().files(
                LintDetectorTest.kotlin(
                        """
            package androidx.lifecycle

            class ViewModel
        """
                ).indented())
                .issues(ISSUE_ANDROID_API_IN_VIEWMODEL)
                .run()
                .expectClean()
    }

    @Test
    fun `A normal class shouldn't trigger the check`() {
        lint().files(
                LintDetectorTest.kotlin(
                        """
            package test

            class TestClass
        """
                ).indented())
                .issues(ISSUE_ANDROID_API_IN_VIEWMODEL)
                .run()
                .expectClean()
    }
}
