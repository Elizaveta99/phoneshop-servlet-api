package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class DefaultCartService implements CartService {

    protected static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private ProductDao productDao = ArrayListProductDao.getInstance();

    private static class SingletonHelper {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return DefaultCartService.SingletonHelper.INSTANCE;
    }

    @Override
    public synchronized Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            session.setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
        }
        return cart;
    }

    @Override
    public synchronized void add(Cart cart, Long productId, int quantity)  throws OutOfStockException {
        if (quantity <= 0) {
            throw new OutOfStockException(null, quantity, 0);
        }

        Product product = productDao.getProduct(productId);
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(c -> product.getId().equals(c.getProduct().getId()))
                .findAny();
        int productsAmount = cartItemOptional.map(CartItem::getQuantity).orElse(0);

        if (product.getStock() < productsAmount + quantity) {
            throw new OutOfStockException(product, productsAmount + quantity, product.getStock());
        }

        if (cartItemOptional.isPresent()) {
            cartItemOptional.get().setQuantity(productsAmount + quantity);
        } else {
            cart.getItems().add(new CartItem(product, quantity));
        }
    }
}
