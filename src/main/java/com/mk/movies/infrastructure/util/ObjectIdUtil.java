package com.mk.movies.infrastructure.util;

import com.mk.movies.infrastructure.exceptions.BadRequestException;
import org.bson.types.ObjectId;

public class ObjectIdUtil {

    public static void validateObjectId(String id) {
        if (!ObjectId.isValid(id)) {
            throw new BadRequestException("Invalid id: " + id);
        }
    }

}
