package com.paulognr.cursor.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper
public interface UUIDMapper {

    default String uuidToString(UUID uuid){
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
}
