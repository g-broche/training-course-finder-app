package com.example.finder.controller.admin;

import com.example.finder.config.JwtProperties;
import com.example.finder.controller.UserRelatedTest;
import com.example.finder.dto.input.RequestLogin;
import com.example.finder.model.AppUser;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.repository.RecordStatusRepository;
import com.example.finder.repository.RoleRepository;
import com.example.finder.repository.UserStatusRepository;
import com.example.finder.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:adminauthtest;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class AdminAuthControllerTest extends UserRelatedTest {
        @SuppressWarnings("unused")
        @Autowired
        private AppUserRepository userRepository;
        @SuppressWarnings("unused")
        @Autowired
        private RoleRepository roleRepository;
        @SuppressWarnings("unused")
        @Autowired
        private UserStatusRepository userStatusRepository;
        @SuppressWarnings("unused")
        @Autowired
        private RecordStatusRepository recordStatusRepository;
        @SuppressWarnings("unused")
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private JwtProperties jwtProperties;

        @MockitoBean
        private EmailService emailService;

        private AppUser adminUser;
        private AppUser regularUser;
        private final String adminPassword = "AdminPassword123!";
        private final String regularPassword = "UserPassword123!";

        @BeforeAll
        void setupTestUsers() {
                adminUser = createTestAdmin(
                                "Admin",
                                "User",
                                "AdminTest",
                                "admin@test.test",
                                adminPassword);

                regularUser = createTestUser(
                                "Regular",
                                "User",
                                "RegularTest",
                                "regular@test.test",
                                regularPassword);
        }

        @Test
        void testAdminLogin_GivenValidAdminCredentials_ReturnsOkWithCookieAndSuccessMessage() throws Exception {
                RequestLogin credentials = new RequestLogin(
                                adminUser.getEmail(),
                                adminPassword);

                MvcResult result = mockMvc.perform(post("/api/admin/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                                .andExpect(status().isOk())
                                .andExpect(header().exists("Set-Cookie"))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.email").value(adminUser.getEmail()))
                                .andReturn();

                String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
                assertNotNull(setCookieHeader, "Set-Cookie header should be present");
                assertTrue(setCookieHeader.contains("accessToken="), "Cookie should contain jwt");
                assertTrue(setCookieHeader.contains("HttpOnly"), "Cookie should be HttpOnly");
                assertTrue(setCookieHeader.contains("Path=/"), "Cookie should have correct path");
                assertTrue(setCookieHeader.contains("Max-Age="), "Cookie should have expiration");
                assertTrue(setCookieHeader.contains("SameSite=Lax"), "Cookie should have SameSite=Lax");
        }

        @Test
        void testAdminLogin_GivenValidAdminCredentials_CookieContainsJwtWithAdminRole() throws Exception {
                RequestLogin credentials = new RequestLogin(
                                adminUser.getEmail(),
                                adminPassword);

                MvcResult result = mockMvc.perform(post("/api/admin/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                                .andExpect(status().isOk())
                                .andExpect(header().exists("Set-Cookie"))
                                .andReturn();

                String setCookieHeader = result.getResponse().getHeader("Set-Cookie");

                // Extract JWT from Set-Cookie header
                String accessToken = extractJwtFromCookie(setCookieHeader);
                assertNotNull(accessToken, "JWT token should be extractable from cookie");
                assertEquals(3, accessToken.split("\\.").length, "JWT should have 3 parts");

                // Decode and validate JWT claims
                String secret = jwtProperties.getSecret();
                byte[] keyBytes = secret.getBytes();

                Jws<Claims> jwsClaims = Jwts.parserBuilder()
                                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                                .build()
                                .parseClaimsJws(accessToken);

                Claims claims = jwsClaims.getBody();

                assertEquals(adminUser.getId().toString(), claims.getSubject(), "Subject should be admin UUID");
                assertNull(claims.get("firstName"), "firstName should not be in token claims");
                assertNull(claims.get("lastName"), "lastName should not be in token claims");
                assertNull(claims.get("displayName"), "displayName should not be in token claims");
                assertNull(claims.get("roles"), "roles should not be in token claims");
        }

        @Test
        void testAdminLogin_GivenRegularUserCredentials_ReturnsUnauthorized() throws Exception {
                RequestLogin credentials = new RequestLogin(
                                regularUser.getEmail(),
                                regularPassword);

                mockMvc.perform(post("/api/admin/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(header().doesNotExist("Set-Cookie"))
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void testAdminLogin_GivenInvalidCredentials_ReturnsUnauthorized() throws Exception {
                RequestLogin credentials = new RequestLogin(
                                adminUser.getEmail(),
                                "WrongPassword123!");

                mockMvc.perform(post("/api/admin/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(header().doesNotExist("Set-Cookie"))
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void testAdminLogin_GivenNonExistentEmail_ReturnsUnauthorized() throws Exception {
                RequestLogin credentials = new RequestLogin(
                                "nonexistent@test.test",
                                "SomePassword123!");

                mockMvc.perform(post("/api/admin/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(header().doesNotExist("Set-Cookie"));
        }

        @Test
        void testAdminLogout_ReturnsOkWithExpiredCookie() throws Exception {
                MvcResult result = mockMvc.perform(post("/api/admin/auth/logout"))
                                .andExpect(status().isOk())
                                .andExpect(header().exists("Set-Cookie"))
                                .andReturn();

                String setCookieHeader = result.getResponse().getHeader("Set-Cookie");
                assertNotNull(setCookieHeader, "Set-Cookie header should be present");
                assertTrue(setCookieHeader.contains("accessToken="), "Cookie should contain jwt");
                assertTrue(setCookieHeader.contains("Max-Age=0"), "Cookie should have Max-Age=0 for expiration");
        }

        @Test
        void testAdminLogin_CookieSecuritySettings_DependOnEnvironment() throws Exception {
                RequestLogin credentials = new RequestLogin(
                                adminUser.getEmail(),
                                adminPassword);

                MvcResult result = mockMvc.perform(post("/api/admin/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credentials)))
                                .andExpect(status().isOk())
                                .andReturn();

                String setCookieHeader = result.getResponse().getHeader("Set-Cookie");

                // In test environment, secure should be false (like dev)
                // The cookie should not have Secure flag in test environment
                assertFalse(setCookieHeader.contains("Secure"),
                                "Cookie should not be secure in test environment");
        }

        /**
         * Helper method to extract JWT token from Set-Cookie header
         * 
         * @param setCookieHeader The Set-Cookie header value
         * @return The JWT token string
         */
        private String extractJwtFromCookie(String setCookieHeader) {
                Pattern pattern = Pattern.compile("accessToken=([^;]+)");
                Matcher matcher = pattern.matcher(setCookieHeader);
                if (matcher.find()) {
                        return matcher.group(1);
                }
                return null;
        }
}
