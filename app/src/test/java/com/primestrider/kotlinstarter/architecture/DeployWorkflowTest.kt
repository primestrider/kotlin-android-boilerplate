package com.primestrider.kotlinstarter.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Guards of the Firebase App Distribution workflow that are easy to lose in an edit and costly when lost. */
class DeployWorkflowTest {
    private val workflow by lazy { File(repoRoot, ".github/workflows/deploy-android.yml").readText() }

    private fun step(name: String): String {
        val start = workflow.indexOf("- name: $name")
        assertTrue("step \"$name\" missing", start >= 0)
        val next = workflow.indexOf("\n      - ", start + 1).let { if (it < 0) workflow.length else it }
        return workflow.substring(start, next)
    }

    @Test
    fun bump_only_on_main() {
        val plan = step("Resolve version bump")
        assertTrue(plan.contains("\"\$GITHUB_REF_NAME\" != \"main\""))
        assertTrue(plan.contains("BUMP=none"))
    }

    @Test
    fun fails_on_debug_signature() {
        val verify = step("Verify APK is signed with the release key")
        assertTrue(verify.contains("CN=Android Debug") && verify.contains("exit 1"))
    }

    @Test
    fun version_commit_skips_ci() {
        assertTrue(workflow.contains("[skip ci]"))
        assertTrue(workflow.contains("!startsWith(github.event.head_commit.message, 'chore(release):')"))
    }

    @Test
    fun tag_after_distribution() {
        assertTrue(workflow.indexOf("- name: Tag released version") > workflow.indexOf("- name: Distribute to Firebase App Distribution"))
        assertTrue(workflow.indexOf("- name: Commit version bump") < workflow.indexOf("- name: Build release APK"))
    }

    @Test
    fun uses_existing_secret_names() {
        listOf(
            "secrets.ANDROID_KEYSTORE_BASE64",
            "secrets.ANDROID_KEYSTORE_PASSWORD",
            "secrets.ANDROID_KEY_ALIAS",
            "secrets.ANDROID_KEY_PASSWORD",
            "secrets.FIREBASE_SERVICE_ACCOUNT_JSON",
            "vars.FIREBASE_ANDROID_APP_ID",
            "vars.FIREBASE_TESTER_GROUPS",
        ).forEach { assertTrue("$it missing", workflow.contains(it)) }
    }
}
