package com.example.finder;

import com.example.finder.model.Category;
import com.example.finder.model.Role;
import com.example.finder.repository.*;
import com.example.finder.seeders.CategorySeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

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
	@Autowired
	UserStatusRepository userStatusRepository;
	@Autowired
	RecordStatusRepository recordStatusRepository;
	@Autowired
	InteractivityStateRepository interactivityStateRepository;
	@Autowired
	AnnounceTypeRepository announceTypeRepository;
	@Autowired
	AnnounceStatusRepository announceStatusRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CategorySeeder categorySeeder;

	@Test
	void contextLoads() {
	}

	@Test
	void rolesAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> roleRepository.getAdminRoleOrThrow(),
				"'Admin' role should exist due to seeding");
		assertDoesNotThrow(
				() -> roleRepository.getUserRoleOrThrow(),
				"'User' role should exist due to seeding");
	}

	@Test
	void userStatusAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> userStatusRepository.getAllowedUserStatusOrThrow(),
				"'Allowed' user status should exist due to seeding");
		assertDoesNotThrow(
				() -> userStatusRepository.getBannedUserStatusOrThrow(),
				"'Banned' user status should exist due to seeding");
	}

	@Test
	void recordStatusAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> recordStatusRepository.getShownRecordStatusOrThrow(),
				"'Shown' record status should exist due to seeding");
		assertDoesNotThrow(
				() -> recordStatusRepository.getHiddenRecordStatusOrThrow(),
				"'Hidden' record status should exist due to seeding");
		assertDoesNotThrow(
				() -> recordStatusRepository.getToDeleteRecordStatusOrThrow(),
				"'To delete' record status should exist due to seeding");
	}

	@Test
	void interactivityStatesAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> interactivityStateRepository.getOpenInteractivityStateOrThrow(),
				"'Open' interactivity state should exist due to seeding");
		assertDoesNotThrow(
				() -> interactivityStateRepository.getCloseInteractivityStateOrThrow(),
				"'Close' interactivity state should exist due to seeding");
	}

	@Test
	void announceTypesAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> announceTypeRepository.getFoundAnnounceTypeOrThrow(),
				"'Found' announce type should exist due to seeding");
		assertDoesNotThrow(
				() -> announceTypeRepository.getLostAnnounceTypeOrThrow(),
				"'Lost' announce type state should exist due to seeding");
	}

	@Test
	void announceStatusAreSeededInDatabase(){
		assertDoesNotThrow(
				() -> announceStatusRepository.getSolvedAnnounceStatusOrThrow(),
				"'Solved' announce type should exist due to seeding");
		assertDoesNotThrow(
				() -> announceStatusRepository.getUnsolvedAnnounceStatusOrThrow(),
				"'Unsolved' announce type state should exist due to seeding");
	}

	@Test
	void databaseHasCategories(){
		List<Category> foundCategories = categoryRepository.findAll();
		assertFalse(foundCategories.isEmpty());
	}
}
