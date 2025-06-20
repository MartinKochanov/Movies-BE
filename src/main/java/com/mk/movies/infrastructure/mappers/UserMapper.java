package com.mk.movies.infrastructure.mappers;

import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.dto.UserRequest;
import com.mk.movies.domain.user.dto.UserUpdateRequest;
import com.mk.movies.domain.user.dto.UserView;
import com.mk.movies.infrastructure.util.ObjectIdMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    uses = {ObjectIdMapperUtil.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "CLIENT")
    User toDocument(UserRequest userRequest);

    @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
    UserView toView(User user);

    void updateDocument(UserUpdateRequest userRequest,@MappingTarget() User user);
}
