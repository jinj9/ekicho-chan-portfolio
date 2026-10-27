package com.ekichochan.app.ui.login

enum class RegistrationIssue { NAME_INVALID, EMAIL_INVALID, PASSWORD_INVALID, PASSWORD_MISMATCH, TOKEN_REQUIRED }

fun validateRegistration(name: String, email: String, password: String, confirmation: String): RegistrationIssue? {
    val nameLength = name.trim().codePointCount(0, name.trim().length)
    if (nameLength !in 1..30 || name.any(Char::isISOControl)) return RegistrationIssue.NAME_INVALID
    if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email.trim())) return RegistrationIssue.EMAIL_INVALID
    if (!Regex("^[!-~]{15,128}$").matches(password)) return RegistrationIssue.PASSWORD_INVALID
    if (password != confirmation) return RegistrationIssue.PASSWORD_MISMATCH
    return null
}

