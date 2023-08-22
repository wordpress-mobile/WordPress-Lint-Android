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
import org.jetbrains.uast.UAnnotationMethod
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UEnumConstant
import org.jetbrains.uast.UField
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UParameter
import org.jetbrains.uast.UVariable

class MissingNullAnnotationDetector : Detector(), SourceCodeScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
            UField::class.java,
            UMethod::class.java,
    )

    override fun createUastHandler(context: JavaContext): UElementHandler? = with(context) {
        if (!isJava(uastFile?.sourcePsi)) {
            return UElementHandler.NONE
        }
        object : UElementHandler() {
            override fun visitField(node: UField) {
                if (node.requiresNullAnnotation && !node.isNullAnnotated) {
                    report(node, MISSING_FIELD_ANNOTATION)
                }
            }

            override fun visitMethod(node: UMethod) {
                node.uastParameters.forEach { visitParameter(node, it) }

                if (node.requiresNullAnnotation && !node.isNullAnnotated) {
                    report(node, MISSING_METHOD_RETURN_TYPE_ANNOTATION)
                }
            }

            private fun visitParameter(node: UMethod, parameter: UParameter) {
                if (parameter.requiresNullAnnotation && !parameter.isNullAnnotated) {
                    if (node.isConstructor) {
                        report(parameter, MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION)
                    } else {
                        report(parameter, MISSING_METHOD_PARAMETER_ANNOTATION)
                    }
                }
            }
        }
    }

    companion object {
        val MISSING_FIELD_ANNOTATION = Issue.create(
                id = "MissingNullAnnotationOnField",
                briefDescription = "Nullable/NonNull annotation missing on field",
                explanation = "Checks for missing `@NonNull/@Nullable` annotations for fields.",
        )
        val MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION = Issue.create(
                id = "MissingNullAnnotationOnConstructorParameter",
                briefDescription = "Nullable/NonNull annotation missing on constructor parameter",
                explanation = "Checks for missing `@NonNull/@Nullable` annotations on constructor parameters.",
        )
        val MISSING_METHOD_PARAMETER_ANNOTATION = Issue.create(
                id = "MissingNullAnnotationOnMethodParameter",
                briefDescription = "Nullable/NonNull annotation missing on method parameter",
                explanation = "Checks for missing `@NonNull/@Nullable` annotations on method parameters.",
        )
        val MISSING_METHOD_RETURN_TYPE_ANNOTATION = Issue.create(
                id = "MissingNullAnnotationOnMethodReturnType",
                briefDescription = "Nullable/NonNull annotation missing on method return type",
                explanation = "Checks for missing `@NonNull/@Nullable` annotations on method return types.",
        )

        val acceptableNullAnnotations = listOf(
                "androidx.annotation.NonNull",
                "androidx.annotation.Nullable",
        )
    }
}

/* UVariable Extensions */
private val UVariable.isPrimitive
    get() = type is PsiPrimitiveType
private val UVariable.isEnum
    get() = this is UEnumConstant
private val UVariable.isInjected
    get() = annotations.any { annotation ->
        annotation.qualifiedName?.endsWith("Inject") ?: false
    }
private val UVariable.isConstant
    get() = isStatic && isFinal
private val UVariable.isInitializedFinalField
    get() = isFinal && uastInitializer != null
private val UVariable.requiresNullAnnotation
    get() = !(isPrimitive || isEnum || isConstant || isInitializedFinalField || isInjected)

/* UMethod Extensions */
private val UMethod.isPrimitive
    get() = returnType is PsiPrimitiveType
private val UMethod.requiresNullAnnotation
    get() = this !is UAnnotationMethod && !isPrimitive && !isConstructor

/* UAnnotated Extensions */
private val UAnnotated.isNullAnnotated
    get() = uAnnotations.any { annotation ->
        MissingNullAnnotationDetector.acceptableNullAnnotations.any { nullAnnotation ->
            annotation.qualifiedName == nullAnnotation
        }
    }

/* Issue.Companion Extensions */
private fun Issue.Companion.create(
        id: String,
        briefDescription: String,
        explanation: String,
) = create(
        id = id,
        briefDescription = briefDescription,
        explanation = explanation,
        category = CORRECTNESS,
        priority = 5,
        severity = Severity.INFORMATIONAL,
        implementation = Implementation(
                MissingNullAnnotationDetector::class.java,
                JAVA_FILE_SCOPE,
        )
)

/* JavaContext Extensions */
private fun JavaContext.report(node: UElement, issue: Issue) = report(
        issue,
        node,
        getLocation(node),
        "Missing null annotation",
        node.fixes,
)

/* UElement Extensions */
private val UElement.fixes
    get() = asSourceString().let { sourceString ->
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