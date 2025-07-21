package org.wordpress.android.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import slack.lint.SlackIssueRegistry
import slack.lint.mocking.DataClassMockDetector
import slack.lint.mocking.RecordClassMockDetector
import slack.lint.mocking.SealedClassMockDetector

class WordPressIssueRegistry : IssueRegistry() {

    private val slackIssueRegistry = SlackIssueRegistry()

    override val api = CURRENT_API
    override val minApi = MIN_API
    override val issues: List<Issue>
        get() {
            val allOwnIssues = listOf(
                MissingNullAnnotationDetector.MISSING_FIELD_ANNOTATION,
                MissingNullAnnotationDetector.MISSING_CONSTRUCTOR_PARAMETER_ANNOTATION,
                MissingNullAnnotationDetector.MISSING_METHOD_PARAMETER_ANNOTATION,
                MissingNullAnnotationDetector.MISSING_METHOD_RETURN_TYPE_ANNOTATION
            )

            val allSlackIssues = slackIssueRegistry.issues
            val selectedSlackIssues = allSlackIssues.filter { issue ->
                issue.id == DataClassMockDetector.issue.id ||
                        issue.id == SealedClassMockDetector.issue.id ||
                        issue.id == RecordClassMockDetector.issue.id
            }

            return allOwnIssues + selectedSlackIssues
        }

    override val vendor = Vendor(
        vendorName = "WordPress Android",
        feedbackUrl = "https://github.com/wordpress-mobile/WordPress-Lint-Android/issues",
        identifier = "org.wordpress.android.lint",
    )

    companion object {
        const val MIN_API = 10
    }
}
