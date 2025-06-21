package com.mk.movies.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleUpdateRequest;
import com.mk.movies.domain.role.dto.RoleView;
import com.mk.movies.domain.role.repository.RoleRepository;
import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.repository.UserRepository;
import com.mk.movies.util.JwtTestUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerTest {

    private static final String BASE_URL = "/api/v1/roles";
    private static Role role;
    private static RoleView roleView;
    private static RoleUpdateRequest roleUpdateRequest;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTestUtil jwtTestUtil;

    private static User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = userRepository.findByEmail("superadmin@example.com")
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

        role = new Role();
        role.setId(new ObjectId());
        role.setName("Po2");

        roleView = new RoleView(role.getId().toHexString(), role.getName());

        roleUpdateRequest = new RoleUpdateRequest("Po", null);

        roleRepository.save(role);
    }

    @Test
    void update_returns_ok_givenValidData() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/" + role.getId())
                .header("Authorization", jwtTestUtil.generateToken(adminUser))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(roleUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(roleView.id()))
            .andExpect(jsonPath("$.name").value(roleUpdateRequest.name()));
    }

    @Test
    void update_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/" + new ObjectId().toHexString())
                .header("Authorization", jwtTestUtil.generateToken(adminUser))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(roleUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/invalid-id")
                .header("Authorization", jwtTestUtil.generateToken(adminUser))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(roleUpdateRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returns_no_content_givenValidId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + role.getId())
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + new ObjectId().toHexString())
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/invalid-id")
                .header("Authorization", jwtTestUtil.generateToken(adminUser)))
            .andExpect(status().isBadRequest());
    }
}