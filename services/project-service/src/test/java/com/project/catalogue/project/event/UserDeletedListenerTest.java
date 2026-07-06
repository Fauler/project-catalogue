package com.project.catalogue.project.event;

import com.project.catalogue.project.boundary.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDeletedListenerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private UserDeletedListener listener;

    @Test
    void onUserDeleted_delegatesToService() {
        UserDeletedEvent event = new UserDeletedEvent(10L, "john@example.com", Instant.now());

        listener.onUserDeleted(event);

        verify(projectService).deleteAllByUserId(10L);
    }
}
