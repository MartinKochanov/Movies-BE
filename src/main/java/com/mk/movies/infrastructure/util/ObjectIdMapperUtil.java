package com.mk.movies.infrastructure.util;

import org.bson.types.ObjectId;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ObjectIdMapperUtil {

    @Named("objectIdToString")
    public static String objectIdToString(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }
}
