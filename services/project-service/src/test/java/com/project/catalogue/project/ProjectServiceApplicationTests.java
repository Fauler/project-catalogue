package com.project.catalogue.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"jwt.secret=bG9jYWwtZGV2LXNlY3JldC1rZXktZm9yLXByb2plY3QtY2F0YWxvZ3VlLXBvYw==",
		"spring.datasource.url=jdbc:h2:mem:project-service-test"
})
class ProjectServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

