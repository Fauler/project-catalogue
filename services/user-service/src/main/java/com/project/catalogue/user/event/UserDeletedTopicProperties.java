package com.project.catalogue.user.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalogue.events.user-deleted")
public record UserDeletedTopicProperties(
        String topic,
        Integer partitions,
        Short replicas
) {
    public UserDeletedTopicProperties {
        if (topic == null || topic.isBlank()) {
            topic = "user.deleted";
        }
        if (partitions == null) {
            partitions = 1;
        }
        if (replicas == null) {
            replicas = 1;
        }
    }
}
