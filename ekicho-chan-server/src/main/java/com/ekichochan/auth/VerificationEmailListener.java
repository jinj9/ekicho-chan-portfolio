package com.ekichochan.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class VerificationEmailListener {
    private static final Logger log = LoggerFactory.getLogger(VerificationEmailListener.class);
    private final JavaMailSender mailSender;
    private final String from;

    public VerificationEmailListener(JavaMailSender mailSender,
            @Value("${app.auth.mail.from:no-reply@ekicho-chan.invalid}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(VerificationEmailRequested event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.email());
            message.setSubject("えきちょーちゃん メールアドレス確認");
            message.setText("次の確認トークンをアプリへ入力してください。\n\n"
                    + event.rawToken() + "\n\n有効期限は24時間です。");
            mailSender.send(message);
        } catch (MailException exception) {
            // Never include the address or token in logs.
            log.error("Verification email delivery failed");
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordReset(PasswordResetEmailRequested event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.email());
            message.setSubject("えきちょーちゃん パスワード再設定");
            message.setText("次の再設定トークンをアプリへ入力してください。\n\n"
                    + event.rawToken() + "\n\n有効期限は30分です。");
            mailSender.send(message);
        } catch (MailException exception) {
            log.error("Password reset email delivery failed");
        }
    }
}
