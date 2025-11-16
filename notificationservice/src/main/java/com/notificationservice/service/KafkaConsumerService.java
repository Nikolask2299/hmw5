package com.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.notificationservice.dto.UserActionEvent;



@Service
public class KafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final EmailService emailService;

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-actions", groupId = "notification-group")
    public void handleUserAction(UserActionEvent event) {
        logger.info("Received event: {}", event);
        String operation = event.operation();
        String email = event.email();

        String subject = "";
        String body = "";

        switch (operation) {
            case "CREATE" -> {
                subject = "Аккаунт успешно создан!";
                body = "<h3>Здравствуйте!</h3><p>Ваш аккаунт на сайте был успешно создан.</p>";;
            }
            case "DELETE" -> {
                subject = "Аккаунт удален!";
                body = "<h3>Здравствуйте!</h3><p>Ваш аккаунт на сайте был успешно удален.</p>";
            }
            default -> logger.warn("Unknown operation received: {}", operation);
        }

        emailService.sendEmail(email, subject, body);
    }

}
