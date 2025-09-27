package com.miraimagiclab.novelreadingapp.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {

    fun sendWelcomeEmail(to: String, username: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject("Welcome to Novel Reading App!")
        message.setText("""
            Hi $username,

            Welcome to Novel Reading App! Your account has been successfully created.

            You can now start reading and following your favorite novels.

            Best regards,
            Novel Reading App Team
        """.trimIndent())

        mailSender.send(message)
    }

    fun sendPasswordResetEmail(to: String, resetToken: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject("Password Reset Request")
        message.setText("""
            You have requested to reset your password.

            Please use the following token to reset your password: $resetToken

            If you didn't request this, please ignore this email.

            Best regards,
            Novel Reading App Team
        """.trimIndent())

        mailSender.send(message)
    }
}