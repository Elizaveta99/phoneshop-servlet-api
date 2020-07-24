package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.model.product.Product;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id) throws ProductNotFoundException;
    List<Product> findProducts(String queryProduct, SortField sortField, SortOrder sortOrder);
    void save(Product product) throws ProductNotFoundException;
    void delete(Long id) throws ProductNotFoundException;
}
