package com.paulognr.cursor.service;

import com.paulognr.cursor.api.CursorList;
import com.paulognr.cursor.api.Cursorable;
import com.paulognr.cursor.mapper.PersonMapper;
import com.paulognr.cursor.repository.Person;
import com.paulognr.cursor.repository.PersonPredicate;
import com.paulognr.cursor.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository peopleRepository;

    @Autowired
    private PersonMapper personMapper;

    public CursorList<PersonDTO> search(Cursorable cursorable) {
        Specification<Person> specification = null;
        if (cursorable.getAfter() != null) {
            specification = PersonPredicate.greatherThanId(cursorable.getAfterField("id"));
        } else if (cursorable.getBefore() != null) {
            specification = PersonPredicate.lessThanId(cursorable.getBeforeField("id"));
        }
        return this.personMapper.entitiesToDTOs(this.peopleRepository.search(specification, cursorable));
    }

}
