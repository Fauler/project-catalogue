package com.project.catalogue.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw==",
        "auth.allowed-client-ids=test-client",
        "auth.admin-client-ids=test-client"
})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
