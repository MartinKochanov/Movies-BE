package com.mk.movies.domain.movie_crew_member.repository;

import com.mk.movies.domain.movie_crew_member.document.MovieCrewMember;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieCrewMemberRepository extends MongoRepository<MovieCrewMember, ObjectId> {

}
