package org.wordpress.android.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UImportStatement
import java.util.*

class WordPressAndroidImportInViewModelDetector : Detector(), Detector.UastScanner {
    companion object {
        @JvmStatic
        val ISSUE_ANDROID_IMPORT_IN_VIEWMODEL =
                Issue.create(
                        id = "AndroidImportsInViewModel",
                        briefDescription = "Disallows Android imports from being used inside the " +
                                "ViewModel class.",
                        explanation = "ViewModels shouldn't know anything about the Android " +
                                "framework classes" +
                                ". This improves testability & modularity.",
                        category = Category.CORRECTNESS,
                        priority = 5,
                        severity = Severity.ERROR,
                        implementation = Implementation(
                                WordPressAndroidImportInViewModelDetector::class.java,
                                EnumSet.of(Scope.JAVA_FILE)))

        private val ALLOWED_ANDROID_IMPORTS = listOf("android.R.", "androidx.lifecycle.ViewModel",
                "androidx.lifecycle", "android.text.TextUtils")
        private val DISALLOWED_ANDROID_IMPORTS = listOf("android.", "androidx.")
    }

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    // only ViewModels are processed by this detector
    override fun applicableSuperClasses() = listOf("androidx.lifecycle.ViewModel")

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitImportStatement(node: UImportStatement) {
                node.importReference?.let { import ->
                    val importedClass = import.asRenderString()

                    // The detector ignores the Android imports that are allowed.
                    val isImportAllowed = ALLOWED_ANDROID_IMPORTS.any { allowedImport ->
                        importedClass.startsWith(allowedImport)
                    }

                    if (isImportAllowed)
                        return@let

                    val isImportDisallowed = DISALLOWED_ANDROID_IMPORTS.any { disallowedImport ->
                        importedClass.startsWith(disallowedImport)
                    }

                    // The detector reports imports that violate the no Android import rule.
                    if (isImportDisallowed) {
                        context.report(
                                ISSUE_ANDROID_IMPORT_IN_VIEWMODEL, node,
                                context.getLocation(import),
                                ISSUE_ANDROID_IMPORT_IN_VIEWMODEL.getExplanation(TextFormat.TEXT))
                    }
                }
            }
        }
    }
}
