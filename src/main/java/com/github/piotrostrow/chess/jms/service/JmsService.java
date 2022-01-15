package com.github.piotrostrow.chess.jms.service;

import com.github.piotrostrow.chess.jms.config.JmsConfig;
import com.github.piotrostrow.chess.jms.message.NewUserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsService.class);

    private final JmsTemplate jmsTemplate;

    public JmsService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(Object message) {
        try {
            jmsTemplate.convertAndSend(JmsConfig.QUEUE, message);
        } catch (JmsException e) {
            LOGGER.error("Could not send jms message: {} (code {})", e.getMessage(), e.getErrorCode());
        }
    }
}
