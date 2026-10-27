package com.ekichochan.app.ui.login

enum class DemoFormIssue { NAME_REQUIRED, EMAIL_INVALID, PASSWORD_REQUIRED, PASSWORD_MISMATCH }

// UI確認用の最低限の検証。サーバーの登録条件や認証を代替するものではない。
fun validateDemoForm(register: Boolean, name: String, email: String, password: String, confirmation: String): DemoFormIssue? {
    if (register && name.isBlank()) return DemoFormIssue.NAME_REQUIRED
    if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email.trim())) return DemoFormIssue.EMAIL_INVALID
    if (password.isBlank()) return DemoFormIssue.PASSWORD_REQUIRED
    if (register && password != confirmation) return DemoFormIssue.PASSWORD_MISMATCH
    return null
}
