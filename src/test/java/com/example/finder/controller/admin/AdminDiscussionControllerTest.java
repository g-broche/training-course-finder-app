package com.example.finder.controller.admin;

import com.example.finder.controller.UserRelatedTest;
import com.example.finder.model.*;
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
        "spring.datasource.url=jdbc:h2:mem:admindiscussiontest;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class AdminDiscussionControllerTest extends UserRelatedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DiscussionRepository discussionRepository;

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
    private MessageRepository messageRepository;

    private AppUser adminUser;
    private AppUser regularUser;
    private AppUser anotherUser;
    private String adminToken;
    private String userToken;
    private Category testCategory;
    private List<Discussion> testDiscussions;
    private Announce testAnnounce;
    private Announce hiddenAnnounce;
    private Discussion hiddenDiscussion;

    @BeforeAll
    void setupTestData() {
        // Create users
        adminUser = createTestAdmin(
                "Admin",
                "User",
                "AdminDiscTest",
                "admin@disctest.test",
                "AdminPassword123!");

        regularUser = createTestUser(
                "Regular",
                "User",
                "RegularDiscTest",
                "regular@disctest.test",
                "UserPassword123!");

        anotherUser = createTestUser(
                "Another",
                "User",
                "AnotherDiscTest",
                "another@disctest.test",
                "AnotherPassword123!");

        adminToken = jwtUtil.generateToken(adminUser);
        userToken = jwtUtil.generateToken(regularUser);

        testCategory = new Category("Electronics");
        testCategory = categoryRepository.save(testCategory);

        testAnnounce = createTestAnnounce("Test Announce", false);
        hiddenAnnounce = createTestAnnounce("Hidden Announce", true);

        testDiscussions = createTestDiscussions();
        hiddenDiscussion = createDiscussionWithMessages(hiddenAnnounce, anotherUser, 2);
    }

    private Announce createTestAnnounce(String title, boolean hidden) {
        AnnounceType foundType = announceTypeRepository.getFoundAnnounceTypeOrThrow();
        AnnounceStatus unsolvedStatus = announceStatusRepository.getUnsolvedAnnounceStatusOrThrow();
        RecordStatus recordStatus = hidden
                ? recordStatusRepository.getHiddenRecordStatusOrThrow()
                : recordStatusRepository.getShownRecordStatusOrThrow();
        InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

        Announce announce = new Announce(
                title,
                "Description for " + title,
                LocalDate.now(),
                "Paris",
                "France",
                "48.8566",
                "2.3522");
        announce.setPhoto("test-photo.jpg");
        announce.setAuthor(regularUser);
        announce.setCategory(testCategory);
        announce.setType(foundType);
        announce.setStatus(unsolvedStatus);
        announce.setRecordStatus(recordStatus);
        announce.setInteractivityState(openState);
        return announceRepository.save(announce);
    }

    private List<Discussion> createTestDiscussions() {
        List<Discussion> discussions = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Discussion discussion = createDiscussionWithMessages(testAnnounce, anotherUser, i + 1);
            discussions.add(discussion);
        }

        return discussions;
    }

    private Discussion createDiscussionWithMessages(Announce announce, AppUser interlocutor, int messageCount) {
        InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();
        RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();

        Discussion discussion = new Discussion();
        discussion.setAnnounce(announce);
        discussion.setInterlocutor(interlocutor);
        discussion.setInteractivityState(openState);
        discussion = discussionRepository.save(discussion);

        // Add messages to the discussion
        for (int i = 0; i < messageCount; i++) {
            Message message = new Message();
            message.setContent("Test message " + i + " in discussion");
            message.setIndex(i);
            message.setAuthor(i % 2 == 0 ? announce.getAuthor() : interlocutor);
            message.setDiscussion(discussion);
            message.setRecordStatus(shownStatus);
            messageRepository.save(message);
        }

        return discussion;
    }

    @Test
    void testGetDiscussionDetail_WithAdminToken_ReturnsOk() throws Exception {
        UUID discussionId = testDiscussions.get(0).getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}", discussionId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.discussionId").value(discussionId.toString()));
    }

    @Test
    void testGetDiscussionDetail_WithRegularUserToken_ReturnsForbidden() throws Exception {
        UUID discussionId = testDiscussions.get(0).getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}", discussionId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetDiscussionDetail_WithoutToken_ReturnsForbidden() throws Exception {
        UUID discussionId = testDiscussions.get(0).getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}", discussionId))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetDiscussionDetail_WithNonExistentId_ReturnsError() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/admin/discussions/{uuid}", nonExistentId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPaginatedDiscussions_WithAdminToken_ReturnsAllDiscussions() throws Exception {
        mockMvc.perform(get("/api/admin/discussions/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty());
    }

    @Test
    void testGetPaginatedDiscussions_WithAdminToken_IncludesHiddenDiscussions() throws Exception {
        // Admin should see discussions linked to hidden announces
        mockMvc.perform(get("/api/admin/discussions/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(6)); // 5 regular + 1 hidden
    }

    @Test
    void testGetPaginatedDiscussions_WithPagination_ReturnsCorrectPage() throws Exception {
        mockMvc.perform(get("/api/admin/discussions/paginated")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetPaginatedDiscussions_WithRegularUserToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/discussions/paginated")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPaginatedDiscussions_WithoutToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/discussions/paginated")
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRelatedAnnounce_WithAdminToken_ReturnsAnnounce() throws Exception {
        UUID discussionId = testDiscussions.get(0).getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}/related-announce", discussionId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Announce"));
    }

    @Test
    void testGetRelatedAnnounce_WithHiddenAnnounce_ReturnsAnnounce() throws Exception {
        UUID discussionId = hiddenDiscussion.getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}/related-announce", discussionId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Hidden Announce"));
    }

    @Test
    void testGetRelatedAnnounce_WithRegularUserToken_ReturnsForbidden() throws Exception {
        UUID discussionId = testDiscussions.get(0).getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}/related-announce", discussionId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRelatedAnnounce_WithoutToken_ReturnsForbidden() throws Exception {
        UUID discussionId = testDiscussions.get(0).getId();

        mockMvc.perform(get("/api/admin/discussions/{uuid}/related-announce", discussionId))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRelatedAnnounce_WithNonExistentId_ReturnsError() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/admin/discussions/{uuid}/related-announce", nonExistentId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
