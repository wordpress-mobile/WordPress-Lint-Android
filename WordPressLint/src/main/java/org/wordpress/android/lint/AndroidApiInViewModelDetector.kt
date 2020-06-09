package org.wordpress.android.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UImportStatement
import java.util.*


class AndroidApiInViewModelDetector: Detector(), Detector.UastScanner {
    companion object {
        @JvmStatic
        val ISSUE_ANDROID_API_IN_VIEWMODEL =
                Issue.create(
                        id = "AndroidImportsInViewModel",
                        briefDescription = "Disallows Android APIs from being used inside the ViewModel class.",
                        explanation = "ViewModels shouldn't know anything about the Android framework classes" +
                                ". This improves, testability & modularity.",
                        category = Category.CORRECTNESS,
                        priority = 5,
                        severity = Severity.ERROR,
                        implementation = Implementation(
                                AndroidApiInViewModelDetector::class.java,
                                EnumSet.of(Scope.JAVA_FILE)))

    }

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitImportStatement(node: UImportStatement) {
                node.importReference?.let {

                }
            }
        }
    }

    override fun applicableSuperClasses() = listOf("androidx.lifecycle.ViewModel")
}


