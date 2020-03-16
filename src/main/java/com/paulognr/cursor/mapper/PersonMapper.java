package com.paulognr.cursor.mapper;

import java.util.stream.Collectors;
import com.paulognr.cursor.api.CursorArrayList;
import com.paulognr.cursor.api.CursorList;
import com.paulognr.cursor.repository.Person;
import com.paulognr.cursor.service.PersonDTO;
import org.mapstruct.Mapper;

@Mapper(uses = {UUIDMapper.class})
public interface PersonMapper {

    PersonDTO entityToDTO(Person person);

    default CursorList<PersonDTO> entitiesToDTOs(CursorList<Person> people) {
        CursorList<PersonDTO> response = new CursorArrayList<>();
        if (people != null) {
            response.addAll(people.stream().map(person -> entityToDTO(person)).collect(Collectors.toList()));
            response.setBefore(people.getBefore());
            response.setAfter(people.getAfter());
        }
        return response;
    }
}
