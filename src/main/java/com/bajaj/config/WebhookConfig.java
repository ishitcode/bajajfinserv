package com.bajaj.config;

import com.bajaj.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WebhookConfig {

    private final WebhookService webhookService;

    @Autowired
    public WebhookConfig(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        webhookService.processWebhook();
    }
} 