package com.example.notification_service.service;

import com.example.common_models.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public void processUserEvent(UserEvent event) {
        logger.info("Processing an event: {}", event);

        emailService.send(event.email(), "Notification", event.operation().getMessage());
    }

}
