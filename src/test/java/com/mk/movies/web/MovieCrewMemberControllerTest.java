package com.mk.movies.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MovieCrewMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/movie-crew-members";
    private static MovieCrewMemberRequest movieCrewMemberRequest;

    @BeforeEach
    void setUp() {
        var mockImage = new MockMultipartFile(
            "image",
            "image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        movieCrewMemberRequest = new MovieCrewMemberRequest(
            "First Name",
            "Last Name",
            mockImage
        );
    }

    @Test
    void create_returns_created_givenValidData() throws Exception {
        mockMvc.perform(multipart(BASE_URL)
                .file((MockMultipartFile) movieCrewMemberRequest.image())
                .param("firstName", movieCrewMemberRequest.firstName())
                .param("lastName", movieCrewMemberRequest.lastName()))
            .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvSource({
        ", Last Name", // Missing first name
        "F, Last Name", // First name less than two characters
        "First Name, ", // Missing last name
        "First Name, L" // Last name less than two characters
    })
    void create_returns_bad_request_givenInvalidNames(String firstName, String lastName)
        throws Exception {
        mockMvc.perform(multipart(BASE_URL)
                .file((MockMultipartFile) movieCrewMemberRequest.image())
                .param("firstName", firstName)
                .param("lastName", lastName))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns_bad_request_givenMissingImage() throws Exception {
        mockMvc.perform(multipart(BASE_URL)
                .param("firstName", movieCrewMemberRequest.firstName())
                .param("lastName", movieCrewMemberRequest.lastName()))
            .andExpect(status().isBadRequest());
    }
}