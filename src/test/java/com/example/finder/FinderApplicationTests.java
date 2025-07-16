package com.example.finder;

import com.example.finder.model.Role;
import com.example.finder.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:context;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(properties = "spring.profiles.active=test")
class FinderApplicationTests {
	@Autowired
	RoleRepository roleRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void rolesAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> roleRepository.getAdminRoleOrThrow(),
				"Admin role should exist due to seeding");
		assertDoesNotThrow(
				() -> roleRepository.getUserRoleOrThrow(),
				"User role should exist due to seeding");
	}
}
