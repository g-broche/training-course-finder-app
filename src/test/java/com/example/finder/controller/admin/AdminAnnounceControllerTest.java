package com.example.finder.controller.admin;

import com.example.finder.controller.UserRelatedTest;
import com.example.finder.model.*;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.repository.*;
import com.example.finder.utils.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:adminannouncetest;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class AdminAnnounceControllerTest extends UserRelatedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AnnounceTypeRepository announceTypeRepository;

    @Autowired
    private AnnounceStatusRepository announceStatusRepository;

    @Autowired
    private RecordStatusRepository recordStatusRepository;

    @Autowired
    private InteractivityStateRepository interactivityStateRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    private AppUser adminUser;
    private AppUser regularUser;
    private String adminToken;
    private String userToken;
    private Category testCategory;
    private List<Announce> testAnnounces;
    private Announce hiddenAnnounce;

    @BeforeAll
    void setupTestData() {
        // Create users
        adminUser = createTestAdmin(
                "Admin",
                "User",
                "AdminTest",
                "admin@test.test",
                "AdminPassword123!");

        regularUser = createTestUser(
                "Regular",
                "User",
                "RegularTest",
                "regular@test.test",
                "UserPassword123!");

        adminToken = jwtUtil.generateToken(adminUser);
        userToken = jwtUtil.generateToken(regularUser);

        // Create test category
        testCategory = new Category("Electronics");
        testCategory = categoryRepository.save(testCategory);

        // Create test announces
        testAnnounces = createTestAnnounces();

        // Create a hidden announce
        hiddenAnnounce = createHiddenAnnounce();
    }

    private List<Announce> createTestAnnounces() {
        List<Announce> announces = new ArrayList<>();
        AnnounceType foundType = announceTypeRepository.getFoundAnnounceTypeOrThrow();
        AnnounceStatus unsolvedStatus = announceStatusRepository.getUnsolvedAnnounceStatusOrThrow();
        RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();
        InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

        for (int i = 0; i < 5; i++) {
            Announce announce = new Announce(
                    "Test Announce " + i,
                    "Description for announce " + i,
                    LocalDate.now().minusDays(i),
                    "Paris",
                    "France",
                    "48.8566",
                    "2.3522");
            announce.setPhoto("test-photo-" + i + ".jpg");
            announce.setAuthor(regularUser);
            announce.setCategory(testCategory);
            announce.setType(foundType);
            announce.setStatus(unsolvedStatus);
            announce.setRecordStatus(shownStatus);
            announce.setInteractivityState(openState);
            announces.add(announceRepository.save(announce));
        }
        return announces;
    }

    private Announce createHiddenAnnounce() {
        AnnounceType foundType = announceTypeRepository.getFoundAnnounceTypeOrThrow();
        AnnounceStatus unsolvedStatus = announceStatusRepository.getUnsolvedAnnounceStatusOrThrow();
        RecordStatus hiddenStatus = recordStatusRepository.getHiddenRecordStatusOrThrow();
        InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

        Announce announce = new Announce(
                "Hidden Announce",
                "This announce is hidden",
                LocalDate.now(),
                "Paris",
                "France",
                "48.8566",
                "2.3522");
        announce.setPhoto("hidden-photo.jpg");
        announce.setAuthor(regularUser);
        announce.setCategory(testCategory);
        announce.setType(foundType);
        announce.setStatus(unsolvedStatus);
        announce.setRecordStatus(hiddenStatus);
        announce.setInteractivityState(openState);
        return announceRepository.save(announce);
    }

    @Test
    void testGetAnnounceDetail_WithAdminToken_ReturnsOk() throws Exception {
        UUID announceId = testAnnounces.get(0).getId();

        mockMvc.perform(get("/api/admin/announces/{uuid}", announceId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Announce 0"));
    }

    @Test
    void testGetAnnounceDetail_WithRegularUserToken_ReturnsForbidden() throws Exception {
        UUID announceId = testAnnounces.get(0).getId();

        mockMvc.perform(get("/api/admin/announces/{uuid}", announceId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAnnounceDetail_WithoutToken_ReturnsUnauthorized() throws Exception {
        UUID announceId = testAnnounces.get(0).getId();

        mockMvc.perform(get("/api/admin/announces/{uuid}", announceId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPaginatedAnnounces_WithAdminToken_ReturnsAllAnnounces() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetPaginatedAnnounces_WithAdminToken_IncludesHiddenAnnounces() throws Exception {
        // Admin should see hidden announces
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(6)); // 5 regular + 1 hidden
    }

    @Test
    void testGetPaginatedAnnounces_WithTypeFilter_ReturnsFilteredResults() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("type", AvailableAnnounceTypes.FOUND.getDisplayName())
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetPaginatedAnnounces_WithCategoryFilter_ReturnsFilteredResults() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("categoryId", testCategory.getId().toString())
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetPaginatedAnnounces_WithSearchTerm_ReturnsFilteredResults() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("search", "Test Announce 0")
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetPaginatedAnnounces_WithPagination_ReturnsCorrectPage() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetPaginatedAnnounces_WithRegularUserToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPaginatedAnnounces_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/announces/paginated")
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAnnounceDiscussions_WithAdminToken_ReturnsDiscussions() throws Exception {
        // Create a discussion for the announce
        UUID announceId = testAnnounces.get(0).getId();
        InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

        Discussion discussion = new Discussion();
        discussion.setAnnounce(testAnnounces.get(0));
        discussion.setInterlocutor(adminUser);
        discussion.setInteractivityState(openState);
        discussionRepository.save(discussion);

        mockMvc.perform(get("/api/admin/announces/{uuid}/discussions", announceId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAnnounceDiscussions_WithRegularUserToken_ReturnsForbidden() throws Exception {
        UUID announceId = testAnnounces.get(0).getId();

        mockMvc.perform(get("/api/admin/announces/{uuid}/discussions", announceId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAnnounceDiscussions_WithoutToken_ReturnsUnauthorized() throws Exception {
        UUID announceId = testAnnounces.get(0).getId();

        mockMvc.perform(get("/api/admin/announces/{uuid}/discussions", announceId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAnnounceDiscussions_WithNonExistentAnnounce_ReturnsNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/admin/announces/{uuid}/discussions", nonExistentId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
