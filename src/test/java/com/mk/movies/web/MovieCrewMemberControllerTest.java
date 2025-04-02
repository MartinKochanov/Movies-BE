package com.mk.movies.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mk.movies.domain.movie_crew_member.document.MovieCrewMember;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import com.mk.movies.domain.movie_crew_member.repository.MovieCrewMemberRepository;
import org.bson.types.ObjectId;
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

//TODO: Prevent file upload in tests
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MovieCrewMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MovieCrewMemberRepository movieCrewMemberRepository;

    private static final String BASE_URL = "/movie-crew-members";
    private static MovieCrewMember movieCrewMember;
    private static MovieCrewMemberRequest movieCrewMemberRequest;

    @BeforeEach
    void setUp() {
        movieCrewMemberRepository.deleteAll();

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

        movieCrewMember = new MovieCrewMember();
        movieCrewMember.setFirstName("First Name");
        movieCrewMember.setLastName("Last Name");
        movieCrewMember.setImageUrl("http://localhost:8080/image.jpg");

        movieCrewMemberRepository.save(movieCrewMember);

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

    @Test
    void getAll_returns_ok() throws Exception {
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].firstName").value(movieCrewMember.getFirstName()))
            .andExpect(jsonPath("$.content[0].lastName").value(movieCrewMember.getLastName()))
            .andExpect(jsonPath("$.content[0].imageUrl").value(movieCrewMember.getImageUrl()));
    }

    @Test
    void getById_returns_ok_givenValidId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + movieCrewMember.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value(movieCrewMember.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(movieCrewMember.getLastName()))
            .andExpect(jsonPath("$.imageUrl").value(movieCrewMember.getImageUrl()));
    }

    @Test
    void getById_returns_bad_request_givenInvalidId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/invalid-id"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returns_not_found_givenNonExistentId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + new ObjectId().toHexString()))
            .andExpect(status().isNotFound());
    }
}