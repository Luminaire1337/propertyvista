package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.email.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${propertyvista.mail.from}")
    private String from;

    @Async
    public void sendEmail(EmailDetails emailDetails) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(from);
            helper.setTo(emailDetails.getRecipientEmail());
            helper.setSubject(emailDetails.getSubject());
            helper.setText(emailDetails.getBody(), true); // true indicates HTML
            mailSender.send(mimeMessage);
            log.info("Email sent to {}", emailDetails.getRecipientEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", emailDetails.getRecipientEmail(), e.getMessage());
        }
    }
}
