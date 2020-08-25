package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ItemNotFoundException;

public interface GenericDao<T> {
    T getItem(Long id) throws ItemNotFoundException;
    void save(T item) throws ItemNotFoundException;
    void delete(Long id) throws ItemNotFoundException;
}
