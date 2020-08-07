package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends AbstractProductServlet {

    protected static final String CART_JSP = "/WEB-INF/pages/cart.jsp";
    private CartService cartService;

    public CartPageServlet() {
        super(CART_JSP);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAttributes(request);
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    private void setAttributes(HttpServletRequest request) {
        request.setAttribute("cart", cartService.getCart(request.getSession()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        Map<Long, String> errorAttributes = new HashMap<>();

        for (int i = 0; i < productIds.length; i++) {
            Long productId = getProductIdIfExist(request, response, productIds[i]);

            try {
                int quantity = getQuantity(quantities[i], request);
                cartService.update(cartService.getCart(request.getSession()), productId, quantity);
            } catch (ParseException | OutOfStockException e) {
                handleError(errorAttributes, productId, e);
            }
        }

        if (errorAttributes.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errorAttributes);
            doGet(request, response);
        }
    }

    private Long getProductIdIfExist(HttpServletRequest request, HttpServletResponse response, String productId) throws IOException {
        Long id = null;
        try {
            id = Long.valueOf(productId);
        } catch (NumberFormatException ex) {
            request.setAttribute("message", "Product " + productId + " not found");
            response.sendError(404);
        }
        return id;
    }

    private void handleError(Map<Long, String> errorAttributes, Long productId, Exception e) {
        if (e.getClass().equals(ParseException.class)) {
            errorAttributes.put(productId, "Not a number");
        } else {
            if (((OutOfStockException) e).getStockRequested() <= 0) {
                errorAttributes.put(productId, "Can't be negative or zero");
            } else {
                errorAttributes.put(productId, "Out of stock, max available " + ((OutOfStockException) e).getStockAvailable());
            }
        }
    }

    private int getQuantity(String quantityString,  HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        return numberFormat.parse(quantityString).intValue();
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }
}
