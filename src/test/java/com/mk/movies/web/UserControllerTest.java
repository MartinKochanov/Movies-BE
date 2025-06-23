package com.mk.movies.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.dto.UserRequest;
import com.mk.movies.domain.user.dto.UserUpdateRequest;
import com.mk.movies.domain.user.enums.Role;
import com.mk.movies.domain.user.repository.UserRepository;
import com.mk.movies.util.JwtTestUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserControllerTest {

    private static final String BASE_URL = "/api/v1/users";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTestUtil jwtTestUtil;
    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User user;

    @BeforeEach
    void setUp() {
        adminUser = userRepository.findByEmail("superadmin@example.com")
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("Password123!");
        user.setRole(Role.CLIENT);
        userRepository.save(user);
    }

    @Test
    void create_returns_created_givenValidData() throws Exception {
        var userRequest = new UserRequest(
            "Alice",
            "Smith",
            "alice.smith@example.com",
            "Password123!"
        );
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest))
            )
            .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvSource({
        ", Smith, alice.smith@example.com, Password123!",
        "Bob, , bob.johnson@example.com, Password123!",
        "Charlie, Brown, , Password123!",
        "David, Wilson, superadmin@example.com, Password123!",
        "Eve, Taylor, invalid-email, Password123!",
        "Frank, White, frank.white@example.com, short",
        "Grace, Black, grace.black@example.com, 12345678",
        "Hannah, Green, hannah.green@example.com , A123.123456",
        "Ivy, Blue, ivy.blue@example.com, a123.123456",
        "Jack, Red, jack.red@example.com, Aa123. 123456",
        "Kate, Purple, kate.purple@example.com, abcdefghijAS.",
    })
    void create_returns_bad_request_givenInvalidData(
        String firstName,
        String lastName,
        String email,
        String password
    ) throws Exception {
        var userRequest = new UserRequest(
            firstName,
            lastName,
            email,
            password
        );
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_returns_ok() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getById_returns_ok_givenValidId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + user.getId())
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(user.getLastName()))
            .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void getById_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + new ObjectId().toHexString())
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getById_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/invalid-id")
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_returns_ok_givenValidData() throws Exception {
        var updateRequest = new UserUpdateRequest(
            "Updated",
            "User",
            new MockMultipartFile("image", "profile.jpg", "image/jpeg", new byte[0]),
            Role.CLIENT
        );
        mockMvc.perform(multipart(BASE_URL + "/" + user.getId())
                .file((MockMultipartFile) updateRequest.image())
                .param("firstName", updateRequest.firstName())
                .param("lastName", updateRequest.lastName())
                .param("role", updateRequest.role().name())
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value(updateRequest.firstName()))
            .andExpect(jsonPath("$.lastName").value(updateRequest.lastName()));
    }

    @Test
    void update_returns_bad_request_givenInvalidId() throws Exception {
        var updateRequest = new UserUpdateRequest(
            "Updated",
            "User",
            new MockMultipartFile("image", "profile.jpg", "image/jpeg", new byte[0]),
            Role.CLIENT
        );
        mockMvc.perform(multipart(BASE_URL + "/invalid-id")
                .file((MockMultipartFile) updateRequest.image())
                .param("firstName", updateRequest.firstName())
                .param("lastName", updateRequest.lastName())
                .param("role", updateRequest.role().name())
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_returns_not_found_givenNonExistentId() throws Exception {
        var updateRequest = new UserUpdateRequest(
            "Updated",
            "User",
            new MockMultipartFile("image", "profile.jpg", "image/jpeg", new byte[0]),
            Role.CLIENT
        );
        mockMvc.perform(multipart(BASE_URL + "/" + new ObjectId().toHexString())
                .file((MockMultipartFile) updateRequest.image())
                .param("firstName", updateRequest.firstName())
                .param("lastName", updateRequest.lastName())
                .param("role", updateRequest.role().name())
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns_no_content_givenValidId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + user.getId())
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/invalid-id")
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + new ObjectId().toHexString())
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isNotFound());
    }
}