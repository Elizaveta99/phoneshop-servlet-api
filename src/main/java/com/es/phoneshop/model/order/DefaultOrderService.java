package com.es.phoneshop.model.order;

import com.es.phoneshop.dao.ArrayListOrderDao;
import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService  {
    private OrderDao orderDao = ArrayListOrderDao.getInstance();
    private ProductDao productDao = ArrayListProductDao.getInstance();

    private static class SingletonHelper {
        private static final DefaultOrderService INSTANCE = new DefaultOrderService();
    }

    public static DefaultOrderService getInstance() {
        return DefaultOrderService.SingletonHelper.INSTANCE;
    }

    @Override
    public Order createOrder(Cart cart) {
        Order order = new Order();
        order.setItems(cart.getItems().stream()
                .map(CartItem::new)
                .collect(Collectors.toList()));

        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
        order.getItems().forEach(cartItem -> productDao.updateProductStock(cartItem.getProduct().getId(), cartItem.getQuantity()));
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(10);
    }
}
