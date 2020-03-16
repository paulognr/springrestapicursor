package com.paulognr.cursor.repository;

import java.util.UUID;
import com.paulognr.cursor.api.CursorRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CursorRepository<Person, UUID> {}
