package com.project.catalogue.project.event;

import com.project.catalogue.project.boundary.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserDeletedListener {

    private final ProjectService projectService;

    public UserDeletedListener(ProjectService projectService) {
        this.projectService = projectService;
    }

    @KafkaListener(
            topics = "${catalogue.events.user-deleted.topic:user.deleted}",
            groupId = "${spring.kafka.consumer.group-id:project-service}")
    public void onUserDeleted(UserDeletedEvent event) {
        log.info("Received user-deleted event for userId {}", event.userId());
        projectService.deleteAllByUserId(event.userId());
    }
}
