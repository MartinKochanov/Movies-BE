package com.mk.movies.infrastructure.mappers;

import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    Role toDocument(RoleRequest roleRequest);

}
