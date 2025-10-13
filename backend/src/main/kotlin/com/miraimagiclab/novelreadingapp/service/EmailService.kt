package com.miraimagiclab.novelreadingapp.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import jakarta.mail.internet.MimeMessage

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

    fun sendVerificationEmail(to: String, username: String, verificationToken: String) {
        val verificationUrl = "http://localhost:8080/api/auth/verify-email?token=$verificationToken"

        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(to)
        helper.setSubject("Activate Your Account - Novel Reading App")

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Account Activation</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background-color: #ffffff;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .header h1 {
                        color: #2c3e50;
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .button {
                        display: inline-block;
                        padding: 15px 30px;
                        background-color: #3498db;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                        text-align: center;
                        margin: 20px 0;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        transition: background-color 0.3s;
                    }
                    .button:hover {
                        background-color: #2980b9;
                    }
                    .footer {
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        font-size: 12px;
                        color: #666;
                        text-align: center;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border: 1px solid #ffeaa7;
                        color: #856404;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Novel Reading App!</h1>
                    </div>

                    <div class="content">
                        <p>Hi <strong>$username</strong>,</p>

                        <p>Thank you for signing up for Novel Reading App! Your account has been created successfully.</p>

                        <p>To start reading and enjoying our collection of novels, please activate your account by clicking the button below:</p>

                        <div style="text-align: center;">
                            <a href="$verificationUrl" class="button">Activate My Account</a>
                        </div>

                        <div class="warning">
                            <strong>Important:</strong> This activation link will expire in 24 hours for security reasons.
                        </div>

                        <p>If the button doesn't work, you can also copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 5px; font-family: monospace;">$verificationUrl</p>

                        <p>If you didn't create this account, please ignore this email.</p>
                    </div>

                    <div class="footer">
                        <p>Best regards,<br>The Novel Reading App Team</p>
                        <p>This is an automated message, please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true) // true indicates HTML content

        mailSender.send(mimeMessage)
    }

    fun sendEmail(to: String, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject(subject)
        message.setText(body)

        mailSender.send(message)
    }
}