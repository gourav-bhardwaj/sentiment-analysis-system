package com.sp.user_sentiment_analysis_listener.service;

import com.sp.user_sentiment_analysis_listener.dto.UserDto;
import com.sp.user_sentiment_analysis_listener.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmailAddress;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine,
                         @Value("${spring.mail.username}") String fromEmailAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromEmailAddress = fromEmailAddress;
    }

    public void sendEmail(String subject, String templateName, UserDto user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set Email Details
            helper.setTo(user.email());
            helper.setSubject(subject);
            helper.setFrom(fromEmailAddress);

            // Set Thymeleaf template variables
            Context context = new Context();
            context.setVariable("user", user);

            // Process Thymeleaf Template
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            // Send Email
            mailSender.send(message);
        } catch (MessagingException mex) {
           log.error("Error: ", mex);
        }
    }

}
