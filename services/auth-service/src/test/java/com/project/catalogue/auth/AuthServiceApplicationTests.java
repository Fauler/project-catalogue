package com.project.catalogue.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=bXktand0U2VjcmV0LWZvci1sb2NhbC1wb3J0dWdhbC1saXNib2EtMjAyNg==",
        "auth.allowed-client-ids=test-client",
        "auth.admin-client-ids=test-client"
})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
