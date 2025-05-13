package com.mk.movies.infrastructure.mappers;

import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleRequest;
import com.mk.movies.domain.role.dto.RoleUpdateRequest;
import com.mk.movies.domain.role.dto.RoleView;
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
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    Role toDocument(RoleRequest roleRequest);

    @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
    RoleView toView(Role role);

    void updateDocument(RoleUpdateRequest roleRequest, @MappingTarget Role role);
}
