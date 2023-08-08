package org.wordpress.android.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.isJava
import com.intellij.psi.PsiPrimitiveType
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UEnumConstant
import org.jetbrains.uast.UField
import org.jetbrains.uast.UMethod

class MissingNullAnnotationDetector: Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
            UField::class.java,
//            UMethod::class.java,
    )

    override fun createUastHandler(context: JavaContext): UElementHandler? =
            if (isJava(context.uastFile?.sourcePsi))
                object: UElementHandler() {
                    override fun visitField(node: UField) {
                        if (!node.isAutoSuppressed && !node.isNullAnnotated) {
                            context.report(
                                    MISSING_FIELD_ANNOTATION,
                                    node,
                                    context.getLocation(node),
                                    "Missing null annotation",
                                    node.fixes,
                            )
                        }
                    }
                }
            else
                null

    companion object {
        val MISSING_FIELD_ANNOTATION = Issue.create(
                id = "MissingNullAnnotationOnField",
                briefDescription = "Nullable/NonNull annotation missing on field",
                explanation = "Checks for missing `@NonNull/@Nullable` annotations for fields.",
                category = CORRECTNESS,
                priority = 5,
                severity = Severity.INFORMATIONAL,
                implementation = Implementation(
                        MissingNullAnnotationDetector::class.java,
                        JAVA_FILE_SCOPE,
                )
        )

        @Suppress("ForbiddenComment")
        // TODO: Create a LintFix to normalize our usage to Android's annotations. This can be
        // useful for running Nullability analysis. See:
        // https://developer.android.com/studio/write/annotations#nullability-analysis
        private val acceptableNullAnnotations = listOf(
                "androidx.annotation.NonNull",
                "androidx.annotation.Nullable",
        )

        private val UField.isPrimitive get() = this.type is PsiPrimitiveType
        private val UField.isEnum get() = this is UEnumConstant
        private val UField.isConstant get() = this.isStatic && this.isFinal
        private val UField.isInitializedFinalField get() = this.isFinal && this.uastInitializer != null

        /** We ignore missing null annotations for cases where this evaluates to true. */
        private val UField.isAutoSuppressed get() =
            this.isPrimitive
                    || this.isEnum
                    || this.isConstant
                    || this.isInitializedFinalField
                    || this.annotations.any { annotation ->
                annotation.qualifiedName?.endsWith("Inject") ?: false
            }


        private val UAnnotated.isNullAnnotated get() = this.uAnnotations.any { annotation ->
            acceptableNullAnnotations.any { nullAnnotation ->
                annotation.qualifiedName == nullAnnotation
            }
        }

        private val UElement.fixes get() = this.asSourceString().let { sourceString ->
            val nullableReplacement = "@Nullable $sourceString"
            val nonNullReplacement = "@NonNull $sourceString"

            LintFix.create().group()
                    .add(
                            LintFix.create()
                                    .name("Annotate as @Nullable")
                                    .replace()
                                    .text(sourceString)
                                    .shortenNames()
                                    .reformat(true)
                                    .with(nullableReplacement)
                                    .build()
                    )
                    .add(
                            LintFix.create()
                                    .name("Annotate as @NonNull")
                                    .replace()
                                    .text(sourceString)
                                    .shortenNames()
                                    .reformat(true)
                                    .with(nonNullReplacement)
                                    .build()
                    )
                    .build()
        }
    }
}




