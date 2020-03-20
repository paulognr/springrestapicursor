package com.paulognr.cursor.repository;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import com.paulognr.cursor.api.CursorArrayList;
import com.paulognr.cursor.api.CursorList;
import com.paulognr.cursor.api.CursorRepository;
import com.paulognr.cursor.api.CursorResponse;
import com.paulognr.cursor.api.Cursorable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public abstract class SimpleCursorRepository<T, ID> extends SimpleJpaRepository<T, ID> implements CursorRepository<T, ID> {

    private static final String KEY_ID = "id";

    private JpaEntityInformation entityInformation;

    public SimpleCursorRepository(@Autowired(required = false) JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
    }

    @Override
    public CursorList<T> search(Specification<T> specification, Cursorable cursorable) {
        CursorList<T> response = new CursorArrayList<T>();
        boolean isAfter = false;
        boolean isBefore = false;

        Page<T> page;
        String idFieldName = this.entityInformation.getIdAttribute().getName();
        if (cursorable.getAfter() != null) {
            ID id = getIdValue(cursorable.getAfterField(KEY_ID));
            page = this.findAll(greaterThanId(id),
                    PageRequest.of(0, cursorable.getSize() + 1, Sort.by(idFieldName)));
            isAfter = true;
        } else if (cursorable.getBefore() != null) {
            ID id = getIdValue(cursorable.getBeforeField(KEY_ID));
            page = this.findAll(lessThanId(id),
                    PageRequest.of(0, cursorable.getSize() + 1, Sort.by(Sort.Direction.DESC, idFieldName)));
            isBefore = true;
        } else {
            page = this.findAll(PageRequest.of(0, cursorable.getSize() + 1, Sort.by(idFieldName)));
        }

        if (page.hasContent()) {
            List<T> entities = page.getContent();
            response.addAll(entities.subList(0, Math.min(cursorable.getSize(), entities.size())));

            if (entities.size() > cursorable.getSize()) {
                if (isBefore) {
                    response.setBefore(getEncodedResponse(entities, cursorable, entities.size() - 2));
                    response.setAfter(getEncodedResponse(entities, cursorable, 0));
                } else {
                    response.setAfter(getEncodedResponse(entities, cursorable, entities.size() - 2));
                }
            }

            if (isBefore) {
                Collections.reverse(response);
                response.setAfter(getEncodedResponse(response, cursorable, response.size() - 1));
            }

            if (isAfter) {
                response.setBefore(getEncodedResponse(entities, cursorable, 0));
            }
        }

        return response;
    }

    private String getEncodedResponse(List<T> entities, Cursorable cursorable, int index) {
        T entity = entities.get(index);
        return CursorResponse.of(cursorable.getSize())
                .add(KEY_ID, this.entityInformation.getId(entity))
                .encode();
    }

    public abstract ID getIdValue(String id);

    public abstract Specification<T> lessThanId(ID id);

    public abstract Specification<T> greaterThanId(ID id);
}
