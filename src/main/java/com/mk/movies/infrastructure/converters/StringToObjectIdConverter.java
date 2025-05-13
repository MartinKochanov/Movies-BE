package com.mk.movies.infrastructure.converters;

import com.mk.movies.infrastructure.exceptions.BadRequestException;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

public class StringToObjectIdConverter implements Converter<String, ObjectId> {

    @Override
    public ObjectId convert(@NotNull String source) {
        try {
            return new ObjectId(source);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid id: " + source);
        }
    }
}
