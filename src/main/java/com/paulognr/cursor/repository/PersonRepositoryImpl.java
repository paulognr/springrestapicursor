package com.paulognr.cursor.repository;

import java.util.UUID;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepositoryImpl extends SimpleCursorRepository<Person, UUID> implements PersonRepository {

    public PersonRepositoryImpl(@Autowired(required = false) JpaEntityInformation<Person, UUID> entityInformation,
                                EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(Person.class, entityManager), entityManager);
    }

    @Override
    public UUID getIdValue(String id) {
        return java.util.UUID.fromString(id);
    }

    @Override
    public Specification<Person> lessThanId(UUID id) {
        return (Specification<Person>) (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), id);
    }

    @Override
    public Specification<Person> greaterThanId(UUID id) {
        return (Specification<Person>) (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }
}
