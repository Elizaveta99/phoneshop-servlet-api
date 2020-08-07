package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

}
