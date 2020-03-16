package com.paulognr.cursor.api;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CursorRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    CursorList<T> search(Specification<T> specification, Cursorable cursorable);
}
