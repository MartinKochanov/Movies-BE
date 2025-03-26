package com.mk.movies.infrastructure.mappers;

import com.mk.movies.domain.movie_crew_member.document.MovieCrewMember;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberUpdateRequest;
import com.mk.movies.domain.movie_crew_member.dto.MovieCrewMemberView;
import com.mk.movies.infrastructure.util.ObjectIdMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    uses = ObjectIdMapperUtil.class,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface MovieCrewMemberMapper {

    @Mapping(target = "id", ignore = true)
    MovieCrewMember toDocument(MovieCrewMemberRequest movieCrewMemberRequest);

    @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
    MovieCrewMemberView toView(MovieCrewMember movieCrewMember);

    void updateDocument(MovieCrewMemberUpdateRequest movieCrewMemberRequest,
        @MappingTarget MovieCrewMember movieCrewMember);

}
