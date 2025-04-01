package com.mk.movies.infrastructure.mappers;

import com.mk.movies.domain.movie.document.Movie;
import com.mk.movies.domain.movie.dto.MovieRequest;
import com.mk.movies.domain.movie.dto.MovieSimpleView;
import com.mk.movies.domain.movie.dto.MovieUpdateRequest;
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
public interface MovieMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
    MovieSimpleView toSimpleView(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "trailerUrl", ignore = true)
    Movie toDocument(MovieRequest dto);

    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "trailerUrl", ignore = true)
    void updateDocument(MovieUpdateRequest request, @MappingTarget Movie movie);
}
