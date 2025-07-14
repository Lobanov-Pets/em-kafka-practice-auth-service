package by.lobanov.authservice.service;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;
    private final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                @Value("${app.kafka.topic.confirmation-codes}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendConfirmationCode(String email, String code) {
        String message = String.format("Email: %s, Code: %s", email, code);
        kafkaTemplate.send(topicName, email, message);
        log.info("Sent message to kafka: {}", message);
    }
}
