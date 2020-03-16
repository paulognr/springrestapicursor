package com.paulognr.cursor.repository;

import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class PersonPredicate {

    public static Specification<Person> greatherThanId(String id) {
        return (Specification<Person>) (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), UUID.fromString(id));
    }

    public static Specification<Person> lessThanId(String id) {
        return (Specification<Person>) (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), UUID.fromString(id));
    }
}
