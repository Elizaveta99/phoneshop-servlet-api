package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
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
    public synchronized void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getItem(productId);
        Optional<CartItem> cartItemOptional = getCartItemOptional(cart, product);
        int productsAmount = cartItemOptional.map(CartItem::getQuantity).orElse(0);

        checkQuantity(quantity, productsAmount + quantity, product);

        if (cartItemOptional.isPresent()) {
            cartItemOptional.get().setQuantity(productsAmount + quantity);
        } else {
            cart.getItems().add(new CartItem(product, quantity));
        }
        recalculateCart(cart);
    }

    @Override
    public synchronized void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getItem(productId);
        Optional<CartItem> cartItemOptional = getCartItemOptional(cart, product);

        checkQuantity(quantity, quantity, product);

        cartItemOptional.ifPresent(cartItem -> cartItem.setQuantity(quantity));
        recalculateCart(cart);
    }

    private Optional<CartItem> getCartItemOptional(Cart cart, Product product) {
        return cart.getItems().stream()
                .filter(c -> product.getId().equals(c.getProduct().getId()))
                .findAny();
    }

    private void checkQuantity(int quantity, int newQuantity, Product product) throws OutOfStockException {
        if (quantity <= 0) {
            throw new OutOfStockException(null, quantity, 0);
        }
        if (product.getStock() < newQuantity) {
            throw new OutOfStockException(product, newQuantity, product.getStock());
        }
    }

    @Override
    public synchronized void delete(Cart cart, Long productId) {
        Product product = productDao.getItem(productId);
        Optional<CartItem> cartItemOptional = getCartItemOptional(cart, product);
        if (cartItemOptional.isPresent()) {
            cart.getItems().removeIf(cartItem -> productId.equals(cartItem.getProduct().getId()));
        }
        recalculateCart(cart);
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .mapToInt(Integer::intValue).sum());
        cart.setTotalCost(cart.getItems().stream()
                .map(cartItem -> new BigDecimal(cartItem.getQuantity()).multiply(cartItem.getProduct().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
