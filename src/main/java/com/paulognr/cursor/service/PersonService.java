package com.paulognr.cursor.service;

import com.paulognr.cursor.api.CursorList;
import com.paulognr.cursor.api.Cursorable;
import com.paulognr.cursor.mapper.PersonMapper;
import com.paulognr.cursor.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository peopleRepository;

    @Autowired
    private PersonMapper personMapper;

    public CursorList<PersonDTO> search(Cursorable cursorable) {
        return this.personMapper.entitiesToDTOs(this.peopleRepository.search(null, cursorable));
    }

}
