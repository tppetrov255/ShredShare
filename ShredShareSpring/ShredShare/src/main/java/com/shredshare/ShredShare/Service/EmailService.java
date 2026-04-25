package com.shredshare.ShredShare.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetCodeEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ShredShare - Код за възстановяване на парола");
        message.setText(
                "Здравейте,\n\n" +
                        "Вашият код за възстановяване на парола е: " + code + "\n\n" +
                        "Кодът е валиден 10 минути.\n\n" +
                        "Ако не сте поискали смяна на парола, игнорирайте този имейл.\n\n" +
                        "ShredShare"
        );

        mailSender.send(message);
    }
}
