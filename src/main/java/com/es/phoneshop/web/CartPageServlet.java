package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends AbstractProductServlet {

    protected static final String CART_JSP = "/WEB-INF/pages/cart.jsp";

    public CartPageServlet() {
        super(CART_JSP);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request.getSession()));
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        Map<Long, String> errorAttributes = new HashMap<>();

        if (productIds != null) {
            for (int i = 0; i < productIds.length; i++) {
                Long productId = getProductIdIfExist(request, response, productIds[i]);
                try {
                    int quantity = getQuantity(quantities[i], request);
                    cartService.update(cartService.getCart(request.getSession()), productId, quantity);
                } catch (ParseException | OutOfStockException e) {
                    handleErrors(errorAttributes, productId, e);
                }
            }
        }

        tryUpdate(request, response, errorAttributes);
    }

    private void handleErrors(Map<Long, String> errorAttributes, Long productId, Exception e) {
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

    private void tryUpdate(HttpServletRequest request, HttpServletResponse response, Map<Long, String> errorAttributes) throws IOException, ServletException {
        if (errorAttributes.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errorAttributes);
            request.setAttribute("cart", cartService.getCart(request.getSession()));
            request.getRequestDispatcher(CART_JSP).forward(request, response);
        }
    }

}
