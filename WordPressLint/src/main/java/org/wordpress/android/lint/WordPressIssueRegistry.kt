package org.wordpress.android.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

class WordPressIssueRegistry : IssueRegistry() {
    override val api = CURRENT_API
    override val minApi = MIN_API
    override val issues
        get() = listOf(
                MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION,
                MissingNullAnnotationDetector.MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION,
                MissingNullAnnotationDetector.MISSING_METHOD_PARAMETER_ANNOTATION,
                MissingNullAnnotationDetector.MISSING_METHOD_RETURN_TYPE_ANNOTATION,
        )

    override val vendor = Vendor(
            vendorName = "WordPress Android",
            feedbackUrl = "https://github.com/wordpress-mobile/WordPress-Lint-Android/issues",
            identifier = "org.wordpress.android.lint",
    )

    companion object {
        const val MIN_API = 10
    }
}