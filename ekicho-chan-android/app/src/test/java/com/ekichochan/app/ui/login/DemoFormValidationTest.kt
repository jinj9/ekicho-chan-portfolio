package com.ekichochan.app.ui.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DemoFormValidationTest {
    @Test fun loginAcceptsDemoInput() { assertNull(validateDemoForm(false, "", "demo@example.com", "sample", "")) }
    @Test fun registrationRequiresName() { assertEquals(DemoFormIssue.NAME_REQUIRED, validateDemoForm(true, " ", "demo@example.com", "sample", "sample")) }
    @Test fun rejectsMalformedEmail() { assertEquals(DemoFormIssue.EMAIL_INVALID, validateDemoForm(false, "", "not-an-email", "sample", "")) }
    @Test fun requiresPassword() { assertEquals(DemoFormIssue.PASSWORD_REQUIRED, validateDemoForm(false, "", "demo@example.com", "", "")) }
    @Test fun requiresMatchingConfirmation() { assertEquals(DemoFormIssue.PASSWORD_MISMATCH, validateDemoForm(true, "サンプル", "demo@example.com", "sample", "different")) }
    @Test fun registrationAcceptsMatchingDemoInput() { assertNull(validateDemoForm(true, "サンプル", "demo@example.com", "sample", "sample")) }
}
