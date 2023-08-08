package org.wordpress.android.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import org.wordpress.android.lint.WordPressAndroidImportInViewModelDetector.Companion.ISSUE_ANDROID_IMPORT_IN_VIEWMODEL

class WordPressIssueRegistry : IssueRegistry() {
    override val api = CURRENT_API
    override val minApi = MIN_API
    override val issues get() = listOf(
            WordPressRtlCodeDetector.SET_PADDING,
            WordPressRtlCodeDetector.SET_MARGIN,
            WordPressRtlCodeDetector.GET_PADDING,
            ISSUE_ANDROID_IMPORT_IN_VIEWMODEL,
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