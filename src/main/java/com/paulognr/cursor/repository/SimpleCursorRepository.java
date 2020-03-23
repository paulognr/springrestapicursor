package com.paulognr.cursor.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import com.paulognr.cursor.api.CursorArrayList;
import com.paulognr.cursor.api.CursorException;
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
        boolean idDescending = false;
        if (cursorable.getAfter() != null) {
            ID id = getIdValue(cursorable.getAfterField(Cursorable.KEY_ID));
            idDescending = isIdDescending(cursorable.getSort(), idFieldName);
            page = this.findAll(idDescending ? lessThanId(id) : greaterThanId(id),
                    PageRequest.of(0, cursorable.getSize() + 1, getSort(cursorable.getSort(), Sort.by(idFieldName), false)));
            isAfter = true;
        } else if (cursorable.getBefore() != null) {
            ID id = getIdValue(cursorable.getBeforeField(Cursorable.KEY_ID));
            idDescending = isIdDescending(cursorable.getSort(), idFieldName);
            page = this.findAll(idDescending ? greaterThanId(id) : lessThanId(id),
                    PageRequest.of(0, cursorable.getSize() + 1,
                            getSort(cursorable.getSort(), Sort.by(Sort.Direction.DESC, idFieldName), idDescending)));
            isBefore = true;
        } else {
            page = this.findAll(PageRequest.of(0, cursorable.getSize() + 1,
                    getSort(cursorable.getSort(), Sort.by(idFieldName), false)));
        }

        boolean needReverse = false;
        if (page.hasContent()) {
            List<T> entities = page.getContent();
            response.addAll(entities.subList(0, Math.min(cursorable.getSize(), entities.size())));

            if (entities.size() > cursorable.getSize()) {
                if (isBefore) {
                    if (idDescending) {
                        needReverse = true;
                        response.setBefore(getEncodedResponse(entities, cursorable, entities.size() - 2));
                        response.setAfter(getEncodedResponse(entities, cursorable, 0));
                    } else {
                        response.setBefore(getEncodedResponse(entities, cursorable, entities.size() - 2));
                        response.setAfter(getEncodedResponse(entities, cursorable, 0));
                    }
                } else {
                    response.setAfter(getEncodedResponse(entities, cursorable, entities.size() - 2));
                }
            }

            if (isBefore) {
                needReverse = true;
                response.setAfter(getEncodedResponse(entities, cursorable, 0));
            }

            if (isAfter) {
                response.setBefore(getEncodedResponse(entities, cursorable, 0));
            }
        }

        if (needReverse) {
            Collections.reverse(response);
        }
        return response;
    }

    private String getEncodedResponse(List<T> entities, Cursorable cursorable, int index) {
        T entity = entities.get(index);

        CursorResponse cursorResponse = CursorResponse.of(cursorable.getSize())
                .add(Cursorable.KEY_ID, this.entityInformation.getId(entity));

        if (cursorable.getSort() != null) {
            cursorResponse.add(Cursorable.KEY_SORT, cursorable.getSort().asString());
        }
        return cursorResponse.encode();
    }

    private Sort getSort(com.paulognr.cursor.api.Sort sort, Sort defaultSortId, boolean idDescending) {
        if (sort != null && sort.isSorted()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (int i = 0; i < sort.getOrders().size(); i++) {
                com.paulognr.cursor.api.Sort.Order order = sort.getOrders().get(i);

                if (i + 1 == sort.getOrders().size()) {
                    Sort.Order idDefaultOrder = defaultSortId.getOrderFor(order.getProperty());
                    if (idDefaultOrder == null) {
                        throw new CursorException("@ID must be the last one for sorting");
                    }
                    if (idDefaultOrder.isDescending()) {
                        if (idDescending) {
                            orders.add(Sort.Order.asc(order.getProperty()));
                        } else {
                            orders.add(idDefaultOrder);
                        }
                        continue;
                    }
                }

                if (order.isDescending()) {
                    orders.add(Sort.Order.desc(order.getProperty()));
                }
                orders.add(Sort.Order.asc(order.getProperty()));
            }
            return Sort.by(orders);
        }
        return defaultSortId;
    }

    private boolean isIdDescending(com.paulognr.cursor.api.Sort sort, String idFieldName) {
        if (sort != null && sort.isSorted()) {
            return sort.getOrders().stream().anyMatch(
              order -> idFieldName.equalsIgnoreCase(order.getProperty()) && order.isDescending()
            );
        }
        return false;
    }

    public abstract ID getIdValue(String id);

    public abstract Specification<T> lessThanId(ID id);

    public abstract Specification<T> greaterThanId(ID id);
}
