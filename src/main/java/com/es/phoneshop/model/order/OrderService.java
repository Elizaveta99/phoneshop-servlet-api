package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;

import java.util.List;

public interface OrderService {
    Order createOrder(Cart cart);
    List<PaymentMethod> getPaymentMethods();
    void placeOrder(Order order);
}
