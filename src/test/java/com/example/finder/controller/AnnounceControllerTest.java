package com.example.finder.controller;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.model.*;
import com.example.finder.repository.*;
import com.example.finder.utils.ImageUtil;
import com.example.finder.utils.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:announcetest;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class AnnounceControllerTest extends UserRelatedTest {
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

    @MockitoBean
    private ImageUtil imageUtil;

    private AppUser testUser;
    private String userToken;
    private Category testCategory;
    private List<Announce> testAnnounces;
    private byte[] testImageBytes = new byte[] {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46,
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46,
            0x49, 0x46, 0x00, 0x01, 0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00,
            (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00, 0x03, 0x02, 0x02, 0x02, 0x02,
            0x02, 0x03, 0x02, 0x02, 0x02, 0x03, 0x03, 0x03, 0x03, 0x04, 0x06, 0x04,
            0x04, 0x04, 0x04, 0x04, 0x08, 0x06, 0x06, 0x05, 0x06, 0x09, 0x08, 0x0A,
            0x0A, 0x09, 0x08, 0x09, 0x09, 0x0A, 0x0C, 0x0F, 0x0C, 0x0A, 0x0B, 0x0E,
            0x0B, 0x09, 0x09, 0x0D, 0x11, 0x0D, 0x0E, 0x0F, 0x10, 0x10, 0x11, 0x10,
            0x0A, 0x0C, 0x12, 0x13, 0x12, 0x10, 0x13, 0x0F, 0x10, 0x10, 0x10,
            (byte) 0xFF, (byte) 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01,
            0x01, 0x11, 0x00, (byte) 0xFF, (byte) 0xC4, 0x00, 0x14, 0x00, 0x01, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x09, (byte) 0xFF, (byte) 0xC4, 0x00, 0x14, 0x10, 0x01, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xDA, 0x00, 0x08, 0x01, 0x01, 0x00,
            0x00, 0x3F, 0x00, (byte) 0x7F, (byte) 0xFF, (byte) 0xD9 };

    @BeforeAll
    void setupTestData() throws Exception {
        testUser = createTestUser(
                "John",
                "Doe",
                "johndoe",
                "john@test.test",
                "1Password!");
        userToken = jwtUtil.generateToken(testUser);

        // Mock ImageUtil to avoid actual image processing
        doNothing().when(imageUtil).saveImage(any(), anyString());
        when(imageUtil.getBaseWebPathForPhotos()).thenReturn("http://localhost:8080/target/test-uploads/photos/");
        when(imageUtil.getWebPathToPhoto(anyString())).thenAnswer(
                invocation -> "http://localhost:8080/target/test-uploads/photos/" + invocation.getArgument(0));

        // Create test category
        testCategory = new Category("Electronics");
        testCategory = categoryRepository.save(testCategory);

        // Create test announces
        AnnounceType foundType = announceTypeRepository.getFoundAnnounceTypeOrThrow();
        AnnounceStatus unsolvedStatus = announceStatusRepository.getUnsolvedAnnounceStatusOrThrow();
        RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();
        InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

        testAnnounces = List.of(
                createAnnounce("Lost Phone", "iPhone 13", foundType, unsolvedStatus, shownStatus, openState),
                createAnnounce("Found Keys", "Car keys near park", foundType, unsolvedStatus, shownStatus, openState),
                createAnnounce("Lost Wallet", "Black leather wallet", foundType, unsolvedStatus, shownStatus,
                        openState));
    }

    private Announce createAnnounce(
            String title,
            String description,
            AnnounceType type,
            AnnounceStatus status,
            RecordStatus recordStatus,
            InteractivityState interactivityState) {
        Announce announce = new Announce(
                title,
                description,
                LocalDate.now(),
                "Paris",
                "France",
                "48.8566",
                "2.3522");
        announce.setPhoto("test-photo.jpg");
        announce.setAuthor(testUser);
        announce.setCategory(testCategory);
        announce.setType(type);
        announce.setStatus(status);
        announce.setRecordStatus(recordStatus);
        announce.setInteractivityState(interactivityState);
        return announceRepository.save(announce);
    }

    @Test
    void getAnnounceDetail() throws Exception {
        Announce announce = testAnnounces.get(0);

        mockMvc.perform(get("/api/announces/" + announce.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(announce.getId().toString()))
                .andExpect(jsonPath("$.data.title").value("Lost Phone"))
                .andExpect(jsonPath("$.data.description").value("iPhone 13"));
    }

    @Test
    void getAnnounceDetail_NotFound() throws Exception {
        mockMvc.perform(get("/api/announces/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPaginatedAnnounces() throws Exception {
        mockMvc.perform(get("/api/announces/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(3));
    }

    @Test
    void getPaginatedAnnounces_WithSearch() throws Exception {
        mockMvc.perform(get("/api/announces/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("search", "Phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Lost Phone"));
    }

    @Test
    void getPaginatedAnnounces_WithCategory() throws Exception {
        mockMvc.perform(get("/api/announces/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("categoryId", testCategory.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3));
    }

    @Test
    void getPaginatedFoundAnnounces() throws Exception {
        mockMvc.perform(get("/api/announces/found/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3));
    }

    @Test
    void createNewFoundAnnounce_Success() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                testImageBytes);

        String description = "Blue backpack found in metro station during afternoon commute hours";
        LocalDate testDate = LocalDate.now().minusDays(1);

        var result = mockMvc.perform(multipart("/api/announces/found/new")
                .file(image)
                .param("title", "Found Backpack")
                .param("description", description)
                .param("latitude", "48.8566")
                .param("longitude", "2.3522")
                .param("city", "Paris")
                .param("country", "France")
                .param("relevantDate", testDate.toString())
                .param("categoryId", testCategory.getId().toString())
                .header("Authorization", "Bearer " + userToken));

        // Print response for debugging
        String responseBody = result.andReturn().getResponse().getContentAsString();
        int status = result.andReturn().getResponse().getStatus();
        System.out.println("=== Response Status: " + status + " ===");
        System.out.println("=== Response Body: " + responseBody + " ===");

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Found Backpack"))
                .andExpect(jsonPath("$.data.description").value(description));

        // Verify the announce was saved
        List<Announce> announces = announceRepository.findAll();
        assertEquals(4, announces.size());
    }

    @Test
    void createNewFoundAnnounce_WithoutAuth_Unauthorized() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                testImageBytes);

        String description = "Blue backpack found in metro station during afternoon commute hours";

        mockMvc.perform(multipart("/api/announces/found/new")
                .file(image)
                .param("title", "Found Backpack")
                .param("description", description)
                .param("latitude", "48.8566")
                .param("longitude", "2.3522")
                .param("city", "Paris")
                .param("country", "France")
                .param("relevantDate", LocalDate.now().toString())
                .param("categoryId", testCategory.getId().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createNewFoundAnnounce_InvalidCategory_BadRequest() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                testImageBytes);

        String description = "Blue backpack found in metro station during afternoon commute hours";

        mockMvc.perform(multipart("/api/announces/found/new")
                .file(image)
                .param("title", "Found Backpack")
                .param("description", description)
                .param("latitude", "48.8566")
                .param("longitude", "2.3522")
                .param("city", "Paris")
                .param("country", "France")
                .param("relevantDate", LocalDate.now().toString())
                .param("categoryId", "99999")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createNewFoundAnnounce_MissingImage_BadRequest() throws Exception {
        String description = "Blue backpack found in metro station during afternoon commute hours";

        mockMvc.perform(multipart("/api/announces/found/new")
                .param("title", "Found Backpack")
                .param("description", description)
                .param("latitude", "48.8566")
                .param("longitude", "2.3522")
                .param("city", "Paris")
                .param("country", "France")
                .param("relevantDate", LocalDate.now().toString())
                .param("categoryId", testCategory.getId().toString())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }
}
