package com.mk.movies.domain.movie_crew_member.service;

import static com.mk.movies.infrastructure.util.ObjectIdUtil.validateObjectId;

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
        var imageUrl = minioService.uploadFile("movie-crew-images", movieCrewMemberRequest.image());
        var movieCrewMember = movieCrewMemberMapper.toDocument(movieCrewMemberRequest);
        movieCrewMember.setImageUrl(imageUrl);
        return movieCrewMemberMapper.toView(movieCrewMemberRepository.save(movieCrewMember));
    }

    public Page<MovieCrewMemberView> getAll(Pageable pageable) {
        return movieCrewMemberRepository.findAll(pageable).map(movieCrewMemberMapper::toView);
    }

    public MovieCrewMemberView getById(String id) {
        validateObjectId(id);
        return movieCrewMemberMapper.toView(getMovieCrewMember(new ObjectId(id)));
    }

    private MovieCrewMember getMovieCrewMember(ObjectId id) {
        return movieCrewMemberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Movie crew member with id " + id + " not found"));
    }

    public MovieCrewMemberView update(String id,
        MovieCrewMemberUpdateRequest movieCrewMemberRequest) {
        validateObjectId(id);

        var imageUrl = minioService.uploadFile("movie-crew-images", movieCrewMemberRequest.image());
        var movieCrewMember = getMovieCrewMember(new ObjectId(id));

        movieCrewMemberMapper.updateDocument(movieCrewMemberRequest, movieCrewMember);
        movieCrewMember.setImageUrl(imageUrl);

        movieCrewMemberRepository.save(movieCrewMember);

        return movieCrewMemberMapper.toView(movieCrewMember);
    }

    public void delete(String id) {
        validateObjectId(id);
        var objectId = new ObjectId(id);
        validateMovieCrewMemberExists(objectId);
        movieCrewMemberRepository.deleteById(objectId);
    }

    private void validateMovieCrewMemberExists(ObjectId id) {
        if (!movieCrewMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Movie crew member with id " + id + " not found");
        }
    }
}
