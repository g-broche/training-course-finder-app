package com.example.finder.controller;

import com.example.finder.config.JwtProperties;
import com.example.finder.dto.JwtDto;
import com.example.finder.dto.input.RequestLogin;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.model.AppUser;
import com.example.finder.model.Role;
import com.example.finder.model.enums.AvailableRoles;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.repository.RecordStatusRepository;
import com.example.finder.repository.RoleRepository;
import com.example.finder.repository.UserStatusRepository;
import com.example.finder.response.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:authtest;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private RecordStatusRepository recordStatusRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void testRegister_GivenValidData_CreatesNewUser() throws Exception {
        RequestRegister registerData = new RequestRegister(
                "John",
                "Doe",
                "JohnD",
                "john.doe@test.test",
                "TestPassword1!",
                true
        );

        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwt").exists())
                .andReturn();

        AppUser createdUser = userRepository.findByEmail(registerData.getEmail()).orElseThrow();

        assertEquals("John",createdUser.getFirstName() , "Created user name should match request");
        assertEquals(1, createdUser.getRoles().size() , "Only one role should be attributed at register");

        String defaultRoleName = AvailableRoles.USER.getDisplayName();
        String roleGivenToNewUser = createdUser.getRoles().iterator().next().getName();

        assertEquals(defaultRoleName, roleGivenToNewUser, "attributed role should be default role");
        assertTrue(
                passwordEncoder.matches(registerData.getPassword(), createdUser.getPassword()),
                "Hashed password should match clear password"
        );
    }

    @Test
    void testLogin_GivenValidCredentials_ReturnsOkWithToken() throws Exception {
        AppUser userToLog = new AppUser(
                "John",
                "Doe",
                "JohnLogin",
                "john.doe@testlogin.test",
                passwordEncoder.encode("TestPassword1!")
        );
        Role userRole = roleRepository.getUserRoleOrThrow();
        userToLog.setRoles(Set.of(userRole));
        userToLog.setUserStatus(userStatusRepository.getAllowedUserStatusOrThrow());
        userToLog.setRecordStatus(recordStatusRepository.getShownRecordStatusOrThrow());

        userRepository.save(userToLog);

        RequestLogin credentials = new RequestLogin(
                "john.doe@testlogin.test",
                "TestPassword1!"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwt").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        ApiResponse<JwtDto> apiResponse = objectMapper.readValue(
                responseBody,
                new TypeReference<ApiResponse<JwtDto>>() {}
        );
        String token = apiResponse.getData().getJwt();

        assertEquals(3, token.split("\\.").length, "JWT should have 3 parts");

        // Proceed to assert that token data is what is expected
        String secret = jwtProperties.getSecret();
        byte[] keyBytes = secret.getBytes();

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                .build()
                .parseClaimsJws(token);

        Claims claims = jwsClaims.getBody();
        assertEquals("john.doe@testlogin.test", claims.getSubject(), "email should correspond");

        String uuidStr = claims.get("uuid", String.class);
        UUID uuidInToken = UUID.fromString(uuidStr);
        assertEquals(userToLog.getId(), uuidInToken, "uuid should correspond");

        assertEquals("John", claims.get("firstName", String.class), "firstName should correspond");
        assertEquals("Doe", claims.get("lastName", String.class), "lastName should correspond");
        assertEquals("JohnLogin", claims.get("displayName", String.class), "displayName should correspond");

        List<String> rolesList = claims.get("roles", List.class);
        assertEquals(1, rolesList.size(), "Should have only one role");
        assertEquals(userRole.getName(), rolesList.get(0), "role should match default role");

        assertEquals(userToLog.getCreatedAt().getTime(), claims.get("userCreatedAt", Long.class), "Should have creation timestamp");
        assertNotNull(claims.getIssuedAt(), "Should have time issued");
        assertNotNull(claims.getExpiration(), "Should have expiration date");
        assertTrue(claims.getExpiration().after(new Date()), "Should not have passed expiration date");
    }
}