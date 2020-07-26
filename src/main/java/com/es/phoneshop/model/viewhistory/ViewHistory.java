package com.es.phoneshop.model.viewhistory;

import com.es.phoneshop.model.product.Product;

import java.util.ArrayDeque;
import java.util.Deque;

public class ViewHistory {

    private Deque<Product> lastViewedProducts;

    public ViewHistory() {
        lastViewedProducts = new ArrayDeque<>();
    }

    public Deque<Product> getLastViewedProducts() {
        return lastViewedProducts;
    }
}
