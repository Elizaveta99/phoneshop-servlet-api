package com.es.phoneshop.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;

import java.util.List;

public interface ProductDao extends GenericDao<Product> {
    List<Product> findProducts(String queryProduct, SortField sortField, SortOrder sortOrder);
    void updateProductStock(Long productId, int quantity);
}
