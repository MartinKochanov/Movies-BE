package com.mk.movies.domain.movie_crew_member.service;

import static com.mk.movies.infrastructure.minio.MinioConstants.MOVIE_CREW_IMAGES_BUCKET;
import static com.mk.movies.infrastructure.minio.MinioUtil.extractFileName;

import com.mk.movies.domain.movie_crew_member.document.MovieCrewMember;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberUpdateRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberView;
import com.mk.movies.domain.movie_crew_member.repository.MovieCrewMemberRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.MovieCrewMemberMapper;
import com.mk.movies.infrastructure.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieCrewMemberService {

    private final MovieCrewMemberRepository movieCrewMemberRepository;
    private final MovieCrewMemberMapper movieCrewMemberMapper;
    private final MinioService minioService;

    public MovieCrewMemberView create(MovieCrewMemberRequest movieCrewMemberRequest) {
        var imageUrl = minioService.uploadFile(
            MOVIE_CREW_IMAGES_BUCKET,
            movieCrewMemberRequest.image());

        var movieCrewMember = movieCrewMemberMapper.toDocument(movieCrewMemberRequest);
        movieCrewMember.setImageUrl(imageUrl);
        return movieCrewMemberMapper.toView(movieCrewMemberRepository.save(movieCrewMember));
    }

    public Page<MovieCrewMemberView> getAll(Pageable pageable) {
        return movieCrewMemberRepository.findAll(pageable).map(movieCrewMemberMapper::toView);
    }

    public MovieCrewMemberView getById(ObjectId id) {
        return movieCrewMemberMapper.toView(getMovieCrewMember(id));
    }

    public MovieCrewMemberView update(ObjectId id,
        MovieCrewMemberUpdateRequest movieCrewMemberRequest) {

        var movieCrewMember = getMovieCrewMember(id);

        if (movieCrewMemberRequest.image() != null && !movieCrewMemberRequest.image().isEmpty()) {
            var oldFileName = extractFileName(movieCrewMember.getImageUrl());
            minioService.deleteFile(MOVIE_CREW_IMAGES_BUCKET, oldFileName);

            var newImageUrl = minioService.uploadFile(
                MOVIE_CREW_IMAGES_BUCKET,
                movieCrewMemberRequest.image());
            movieCrewMember.setImageUrl(newImageUrl);
        }

        movieCrewMemberMapper.updateDocument(movieCrewMemberRequest, movieCrewMember);
        movieCrewMemberRepository.save(movieCrewMember);

        return movieCrewMemberMapper.toView(movieCrewMember);
    }

    public void delete(ObjectId id) {

        var movieCrewMember = getMovieCrewMember(id);

        String fileName = extractFileName(movieCrewMember.getImageUrl());

        minioService.deleteFile(MOVIE_CREW_IMAGES_BUCKET, fileName);
        movieCrewMemberRepository.deleteById(id);
    }

    private MovieCrewMember getMovieCrewMember(ObjectId id) {
        return movieCrewMemberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Movie crew member with id " + id + " not found"));
    }
}
