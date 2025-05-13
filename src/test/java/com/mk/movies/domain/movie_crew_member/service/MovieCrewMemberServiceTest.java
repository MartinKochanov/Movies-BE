package com.mk.movies.domain.movie_crew_member.service;

import static com.mk.movies.infrastructure.minio.MinioConstants.MOVIE_CREW_IMAGES_BUCKET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mk.movies.domain.movie_crew_member.document.MovieCrewMember;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberUpdateRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberView;
import com.mk.movies.domain.movie_crew_member.repository.MovieCrewMemberRepository;
import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleView;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.MovieCrewMemberMapper;
import com.mk.movies.infrastructure.minio.MinioService;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MovieCrewMemberServiceTest {

    @Mock
    private MovieCrewMemberRepository movieCrewMemberRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private MovieCrewMemberMapper movieCrewMemberMapper;

    @InjectMocks
    private MovieCrewMemberService movieCrewMemberService;

    private MovieCrewMember movieCrewMember;
    private MovieCrewMemberRequest movieCrewMemberRequest;
    private MovieCrewMemberUpdateRequest movieCrewMemberUpdateRequest;
    private MovieCrewMemberView movieCrewMemberView;
    private Role role;
    private RoleView roleView;

    @BeforeEach
    void setUp() {
        role = new Role();

        role.setId(new ObjectId());
        role.setName("Role");
        role.setCastId(new ObjectId());
        role.setMovieId(new ObjectId());

        roleView = new RoleView(
            role.getId().toHexString(),
            role.getName()
        );

        movieCrewMember = new MovieCrewMember();
        movieCrewMember.setId(new ObjectId());
        movieCrewMember.setFirstName("Name");
        movieCrewMember.setLastName("Last Name");
        movieCrewMember.setImageUrl("http://localhost:8080/image.jpg");
        movieCrewMember.setRolesIds(List.of(role.getId()));

        movieCrewMemberRequest = new MovieCrewMemberRequest(
            "Name",
            "Role",
            new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0])
        );

        movieCrewMemberUpdateRequest = new MovieCrewMemberUpdateRequest(
            "Updated Name",
            "Updated Role",
            new MockMultipartFile("image", "updated_image.jpg", "image/jpeg", new byte[0])
        );

        movieCrewMemberView = new MovieCrewMemberView(
            movieCrewMember.getId().toHexString(),
            "Name",
            "Role",
            "http://localhost:8080/image.jpg",
            roleView
        );
    }

    @Test
    void create_returnsMovieCrewMemberView_whenMovieCrewMemberIsSaved() {
        when(movieCrewMemberMapper.toDocument(movieCrewMemberRequest)).thenReturn(movieCrewMember);
        when(minioService.uploadFile(MOVIE_CREW_IMAGES_BUCKET,
            movieCrewMemberRequest.image())).thenReturn(movieCrewMember.getImageUrl());
        when(movieCrewMemberRepository.save(movieCrewMember)).thenReturn(movieCrewMember);
        when(movieCrewMemberMapper.toView(movieCrewMember)).thenReturn(movieCrewMemberView);

        var result = movieCrewMemberService.create(movieCrewMemberRequest);

        assertEquals(movieCrewMemberView, result);
    }

    @Test
    void getAll_returnsPageOfMovieCrewMemberView() {
        Page<MovieCrewMember> movieCrewMemberPage = new PageImpl<>(List.of(movieCrewMember));
        when(movieCrewMemberRepository.findAll(PageRequest.of(0, 10))).thenReturn(
            movieCrewMemberPage);
        when(movieCrewMemberMapper.toView(movieCrewMember)).thenReturn(movieCrewMemberView);

        var result = movieCrewMemberService.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(movieCrewMemberView, result.getContent().get(0));
    }

    @Test
    void getById_returnsMovieCrewMemberView_whenMovieCrewMemberExists() {
        when(movieCrewMemberRepository.findById(movieCrewMember.getId())).thenReturn(
            Optional.of(movieCrewMember));
        when(movieCrewMemberMapper.toView(movieCrewMember)).thenReturn(movieCrewMemberView);

        var result = movieCrewMemberService.getById(movieCrewMember.getId());

        assertEquals(movieCrewMemberView, result);
    }

    @Test
    void getById_throwsResourceNotFoundException_whenMovieCrewMemberDoesNotExist() {
        when(movieCrewMemberRepository.findById(movieCrewMember.getId())).thenReturn(
            Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> movieCrewMemberService.getById(movieCrewMember.getId()));
    }

    @Test
    void update_returnsUpdatedMovieCrewMemberView_whenMovieCrewMemberIsUpdated() {
        when(movieCrewMemberRepository.findById(movieCrewMember.getId())).thenReturn(
            Optional.of(movieCrewMember));
        when(movieCrewMemberMapper.toView(movieCrewMember)).thenReturn(movieCrewMemberView);

        var result = movieCrewMemberService.update(movieCrewMember.getId(),
            movieCrewMemberUpdateRequest);

        assertEquals(movieCrewMemberView, result);
    }

    @Test
    void delete_deletesMovieCrewMember_whenMovieCrewMemberExists() {
        when(movieCrewMemberRepository.findById(movieCrewMember.getId())).thenReturn(
            Optional.of(movieCrewMember));

        movieCrewMemberService.delete(movieCrewMember.getId());

        verify(movieCrewMemberRepository).deleteById(movieCrewMember.getId());
        verify(minioService).deleteFile(MOVIE_CREW_IMAGES_BUCKET, "image.jpg");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenMovieCrewMemberDoesNotExist() {
        when(movieCrewMemberRepository.findById(movieCrewMember.getId())).thenReturn(
            Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> movieCrewMemberService.delete(movieCrewMember.getId()));
    }
}