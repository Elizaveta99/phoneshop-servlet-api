package com.es.phoneshop.model.viewhistory;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpSession;

public interface ViewHistoryService {
    ViewHistory getViewHistory(HttpSession session);
    void addProductToViewHistory(HttpSession session, Product product);
}
