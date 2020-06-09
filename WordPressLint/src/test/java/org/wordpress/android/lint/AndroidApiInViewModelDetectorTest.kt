package org.wordpress.android.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.junit.Test
import org.wordpress.android.lint.AndroidApiInViewModelDetector.Companion.ISSUE_ANDROID_API_IN_VIEWMODEL
import java.io.File

class AndroidApiInViewModelDetectorTest {
    @Test
    fun `when ViewModel contains Android import then test should fail`() {
        lint().files(
                LintDetectorTest.kotlin(
                        """
            package test
            import android.widget.TextView
            import androidx.lifecycle.ViewModel

            class TestViewModel : ViewModel() {
              lateinit var textView: TextView
             }
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

    fun lint() = TestLintTask().sdkHome(SDK_PATH).detector(AndroidApiInViewModelDetector())


    companion object {
        private val SDK_PATH = File("/Users/joeldean/Library/Android/sdk")
    }
}
