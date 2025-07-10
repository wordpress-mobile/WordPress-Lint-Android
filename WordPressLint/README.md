Below you can read the AI-generated, and human-improved explanation of the shadow JAR configuration, that's
result of [#21](https://github.com/wordpress-mobile/WordPress-Lint-Android/pull/21).

# Shadow JAR Logic Explanation

## Overview
This document explains the logic behind the Shadow JAR configuration in the WordPressLint/build.gradle file, specifically the `shadowJar`, `cleanServiceFile` task, and publishing setup.

## The Problem Being Solved

The WordPress Lint library has a unique requirement:
1. It needs to include **selected** lint rules from Slack's lint library (not all of them)
2. It must bundle all necessary classes into a single JAR file for distribution, **without declaring Slack Lint as a regular dependency**, to prevent Android Lint from loading its full issue registry and enabling all of its checks.
3. It needs to ensure proper service registration with Android Lint framework

## How Android Lint Service Discovery Works

Android Lint uses Java's ServiceLoader mechanism to discover lint rule providers. It looks for files in:
```
META-INF/services/com.android.tools.lint.client.api.IssueRegistry
```

These files contain fully qualified class names of classes that implement `IssueRegistry`.

## The WordPress Approach

### 1. WordPressIssueRegistry Implementation
```kotlin
class WordPressIssueRegistry : IssueRegistry() {
    private val slackIssueRegistry = SlackIssueRegistry()

    override val issues: List<Issue>
        get() {
            val allOwnIssues = listOf(/* WordPress-specific issues */)
            val selectedSlackIssues = slackIssueRegistry.issues.filter { /* only specific ones */ }
            return allOwnIssues + selectedSlackIssues
        }
}
```

**Key Point**: WordPress programmatically creates a SlackIssueRegistry instance and selectively includes only the rules it wants.

### 2. Service Registration
The WordPress service file contains only:
```
org.wordpress.android.lint.WordPressIssueRegistry
```

## The Shadow JAR Process

### Step 1: shadowJar Task
```gradle
shadowJar {
    mergeServiceFiles()
}
```

**What happens:**
- Creates a "fat JAR" containing all dependencies (including Slack lint library)
- `mergeServiceFiles()` combines all `META-INF/services/*` files from all JARs
- **Problem**: This would merge WordPress's service file with Slack's service file, resulting in:
  ```
  org.wordpress.android.lint.WordPressIssueRegistry
  slack.lint.SlackIssueRegistry
  ```

### Step 2: cleanServiceFile Task
```gradle
tasks.register('cleanServiceFile') {
    doLast {
        // Extract the JAR
        // Find the service file
        // Remove lines containing "slack.lint.SlackIssueRegistry"
        // Repackage the JAR
    }
}
```

**What happens:**
- Extracts the shadow JAR to a temporary directory
- Locates the merged service file
- Removes any lines containing `slack.lint.SlackIssueRegistry`
- Repackages the JAR

**Result**: The final JAR contains only:
```
org.wordpress.android.lint.WordPressIssueRegistry
```

## Why This Approach?

### Without the cleanServiceFile task:
1. Both `WordPressIssueRegistry` and `SlackIssueRegistry` would be registered
2. Android Lint would load both registries
3. This could cause conflicts, performance issues, or unexpected behavior. And actually enabling all issues from `SlackIssueRegistry`

### With the cleanServiceFile task:
1. Only `WordPressIssueRegistry` is registered as a service
2. WordPress has full control over which Slack rules are included
3. No duplication or conflicts
4. Clean, predictable behavior

## Publishing Configuration: Why `components["shadow"]` instead of `components.java`?

```gradle
publishing {
    publications {
        maven(MavenPublication) {
            from(components["shadow"])  // Uses the cleaned shadow JAR
            groupId = 'org.wordpress'
            artifactId = 'lint'
        }
    }
}
```

### The Key Difference

**If we used `from components.java`:**
- Would publish only the compiled classes from this project (WordPress lint rules)
- Dependencies would be listed as external dependencies in the POM file
- Consumers would need to resolve and download the Slack lint library separately
- The original service file would be published (not the cleaned one)
- Result: A regular JAR that requires dependency resolution and all Slack rules are enabled

**Using `from(components["shadow"])`:**
- Publishes the complete shadow JAR with all dependencies included (fat JAR)
- All Slack lint classes are embedded directly in the published JAR
- The cleaned service file is included (with SlackIssueRegistry removed)
- Consumers get a single, self-contained JAR with no external dependencies
- Result: A fat JAR ready for immediate use and only selected Slack rules are enabled

### Why This Matters

1. **Not all Slack rules are enabled**: Only selected ones in `WordPressIssueRegistry`
1. **Service File Integrity**: The cleaned service file (after `cleanServiceFile` task) must be the one that gets published, not the original
2. **Self-Contained Distribution**: Consumers don't need to worry about resolving the Slack lint dependency
3. **Controlled Dependency**: The Slack dependency is embedded and controlled, not exposed as a transitive dependency
4. **Simplified Consumption**: One JAR file contains everything needed to run the WordPress lint rules

### The Publishing Flow

1. `shadowJar` task creates the fat JAR with merged dependencies
2. `cleanServiceFile` task removes unwanted service registrations
3. `components["shadow"]` references the final, cleaned shadow JAR
4. Publishing uploads this processed JAR to the repository

This ensures that what gets published is exactly what was intended: a self-contained JAR with WordPress rules + selected Slack rules, with proper service registration.

## Summary

This configuration solves a complex dependency management problem:
- **Goal**: Include selected rules from Slack lint library in WordPress lint
- **Challenge**: Avoid service registration conflicts and rule duplication
- **Solution**: Use Shadow JAR to bundle dependencies, then clean up service files to ensure only WordPress registry is registered
- **Result**: A single JAR with WordPress rules + selected Slack rules, properly registered with Android Lint

The seemingly complex build logic is actually an elegant solution to ensure clean service discovery while reusing valuable lint rules from the Slack library.