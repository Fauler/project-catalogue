package com.project.catalogue.user.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class UserEventPublisher {

    private final KafkaTemplate<String, UserDeletedEvent> kafkaTemplate;
    private final UserDeletedTopicProperties topicProperties;

    public UserEventPublisher(KafkaTemplate<String, UserDeletedEvent> kafkaTemplate,
                              UserDeletedTopicProperties topicProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicProperties = topicProperties;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserDeleted(UserDeletedEvent event) {
        log.info("Publishing {} for userId {}", topicProperties.topic(), event.userId());
        kafkaTemplate.send(topicProperties.topic(), String.valueOf(event.userId()), event);
    }
}
