package dao;

import entity.Currency;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    Optional<Currency> findById(Long id);

    List<T> findAll();

    T save(T entity);

    T update(T entity);

    void delete(Long id);

}
