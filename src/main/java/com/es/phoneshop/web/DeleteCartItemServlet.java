package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteCartItemServlet extends AbstractProductServlet {

    protected static final String DELETECARTITEM_JSP = "/WEB-INF/pages/cart.jsp";

    public DeleteCartItemServlet() {
        super(DELETECARTITEM_JSP);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = getProductIdIfExist(request, response, request.getPathInfo().substring(1));
        Cart cart = cartService.getCart(request.getSession());
        cartService.delete(cart, productId);

        response.sendRedirect(request.getContextPath() + "/cart?message=Cart item with product " + productId + " deleted successfully");
    }
}
