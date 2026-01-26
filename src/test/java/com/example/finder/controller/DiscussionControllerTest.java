package com.example.finder.controller;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:discussiontest;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class DiscussionControllerTest extends UserRelatedTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private DiscussionRepository discussionRepository;

        @Autowired
        private MessageRepository messageRepository;

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

        private AppUser announceAuthor;
        private AppUser discussionInitiator;
        private AppUser otherUser;
        private String authorToken;
        private String initiatorToken;
        private String otherUserToken;
        private Category testCategory;
        private Announce testAnnounce;
        private Discussion testDiscussion;
        private Discussion isolatedDiscussion;

        @BeforeAll
        void setupTestData() {
                // Create test users
                announceAuthor = createTestUser(
                                "Alice",
                                "Author",
                                "alice",
                                "alice@test.test",
                                "1Password!");
                authorToken = jwtUtil.generateToken(announceAuthor);

                discussionInitiator = createTestUser(
                                "Bob",
                                "Initiator",
                                "bob",
                                "bob@test.test",
                                "1Password!");
                initiatorToken = jwtUtil.generateToken(discussionInitiator);

                otherUser = createTestUser(
                                "Charlie",
                                "Other",
                                "charlie",
                                "charlie@test.test",
                                "1Password!");
                otherUserToken = jwtUtil.generateToken(otherUser);

                // Create test category
                testCategory = new Category("Electronics");
                testCategory = categoryRepository.save(testCategory);

                // Create test announce
                AnnounceType foundType = announceTypeRepository.getFoundAnnounceTypeOrThrow();
                AnnounceStatus unsolvedStatus = announceStatusRepository.getUnsolvedAnnounceStatusOrThrow();
                RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();
                InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

                testAnnounce = new Announce(
                                "Lost Laptop",
                                "MacBook Pro lost in coffee shop on Main Street yesterday afternoon",
                                LocalDate.now().minusDays(1),
                                "Paris",
                                "France",
                                "48.8566",
                                "2.3522");
                testAnnounce.setPhoto("test-photo.jpg");
                testAnnounce.setAuthor(announceAuthor);
                testAnnounce.setCategory(testCategory);
                testAnnounce.setType(foundType);
                testAnnounce.setStatus(unsolvedStatus);
                testAnnounce.setRecordStatus(shownStatus);
                testAnnounce.setInteractivityState(openState);
                testAnnounce = announceRepository.save(testAnnounce);

                // Create test discussion
                testDiscussion = new Discussion();
                testDiscussion.setAnnounce(testAnnounce);
                testDiscussion.setInterlocutor(discussionInitiator);
                testDiscussion.setInteractivityState(openState);
                testDiscussion = discussionRepository.save(testDiscussion);

                // Create initial message for testDiscussion
                Message firstMessage = new Message();
                firstMessage.setAuthor(discussionInitiator);
                firstMessage.setDiscussion(testDiscussion);
                firstMessage.setContent("Hello, I think I found your laptop!");
                firstMessage.setIndex(1);
                firstMessage.setRecordStatus(shownStatus);
                firstMessage.setReported(false);
                messageRepository.save(firstMessage);

                // Create isolated discussion for tests that should not be affected by other
                // tests
                isolatedDiscussion = new Discussion();
                isolatedDiscussion.setAnnounce(testAnnounce);
                isolatedDiscussion.setInterlocutor(discussionInitiator);
                isolatedDiscussion.setInteractivityState(openState);
                isolatedDiscussion = discussionRepository.save(isolatedDiscussion);

                // Create initial message for isolatedDiscussion
                Message isolatedMessage = new Message();
                isolatedMessage.setAuthor(discussionInitiator);
                isolatedMessage.setDiscussion(isolatedDiscussion);
                isolatedMessage.setContent("Hello, I think I found your laptop!");
                isolatedMessage.setIndex(1);
                isolatedMessage.setRecordStatus(shownStatus);
                isolatedMessage.setReported(false);
                messageRepository.save(isolatedMessage);
        }

        @Test
        void getDiscussion_AsAnnounceAuthor_Success() throws Exception {
                mockMvc.perform(get("/api/discussions/" + isolatedDiscussion.getId())
                                .header("Authorization", "Bearer " + authorToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.discussionId").value(isolatedDiscussion.getId().toString()))
                                .andExpect(jsonPath("$.data.announceId").value(testAnnounce.getId().toString()))
                                .andExpect(jsonPath("$.data.announceAuthor.displayName").value("alice"))
                                .andExpect(jsonPath("$.data.announceResponder.displayName").value("bob"))
                                .andExpect(jsonPath("$.data.messages").isArray())
                                .andExpect(jsonPath("$.data.messages.length()").value(1))
                                .andExpect(jsonPath("$.data.messages[0].content")
                                                .value("Hello, I think I found your laptop!"));
        }

        @Test
        void getDiscussion_AsDiscussionInitiator_Success() throws Exception {
                mockMvc.perform(get("/api/discussions/" + isolatedDiscussion.getId())
                                .header("Authorization", "Bearer " + initiatorToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.discussionId").value(isolatedDiscussion.getId().toString()))
                                .andExpect(jsonPath("$.data.announceId").value(testAnnounce.getId().toString()))
                                .andExpect(jsonPath("$.data.messages").isArray())
                                .andExpect(jsonPath("$.data.messages.length()").value(1));
        }

        @Test
        void getDiscussion_AsNonParticipant_NotFound() throws Exception {
                mockMvc.perform(get("/api/discussions/" + testDiscussion.getId())
                                .header("Authorization", "Bearer " + otherUserToken))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message")
                                                .value("Either there is no such discussion or you are not a participant in it"));
        }

        @Test
        void getDiscussion_NonExistentId_NotFound() throws Exception {
                mockMvc.perform(get("/api/discussions/00000000-0000-0000-0000-000000000000")
                                .header("Authorization", "Bearer " + authorToken))
                                .andExpect(status().isNotFound());
        }

        @Test
        void getDiscussion_WithoutAuth_Unauthorized() throws Exception {
                mockMvc.perform(get("/api/discussions/" + testDiscussion.getId()))
                                .andExpect(status().isForbidden());
        }

        @Test
        void getDiscussion_VerifyMessageOrder() throws Exception {
                // Add more messages
                RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();

                Message secondMessage = new Message();
                secondMessage.setAuthor(announceAuthor);
                secondMessage.setDiscussion(testDiscussion);
                secondMessage.setContent("Great! Where did you find it?");
                secondMessage.setIndex(2);
                secondMessage.setRecordStatus(shownStatus);
                secondMessage.setReported(false);
                messageRepository.save(secondMessage);

                Message thirdMessage = new Message();
                thirdMessage.setAuthor(discussionInitiator);
                thirdMessage.setDiscussion(testDiscussion);
                thirdMessage.setContent("At the coffee shop on Main Street.");
                thirdMessage.setIndex(3);
                thirdMessage.setRecordStatus(shownStatus);
                thirdMessage.setReported(false);
                messageRepository.save(thirdMessage);

                mockMvc.perform(get("/api/discussions/" + testDiscussion.getId())
                                .header("Authorization", "Bearer " + authorToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.messages").isArray())
                                .andExpect(jsonPath("$.data.messages.length()").value(3))
                                .andExpect(jsonPath("$.data.messages[0].content")
                                                .value("Hello, I think I found your laptop!"))
                                .andExpect(jsonPath("$.data.messages[1].content")
                                                .value("Great! Where did you find it?"))
                                .andExpect(jsonPath("$.data.messages[2].content")
                                                .value("At the coffee shop on Main Street."));
        }
}
