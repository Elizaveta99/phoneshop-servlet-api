package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.servlethelper.DefaultServletHelperService;
import com.es.phoneshop.model.servlethelper.ServletHelperService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {

    private CartService cartService;
    private ServletHelperService servletHelperService;

    public DeleteCartItemServlet() {
        cartService = DefaultCartService.getInstance();
        servletHelperService = DefaultServletHelperService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = servletHelperService.getProductIdIfExist(request, response, request.getPathInfo().substring(1));
        Cart cart = cartService.getCart(request.getSession());
        cartService.delete(cart, productId);

        response.sendRedirect(request.getContextPath() + "/cart?message=Cart item with product " + productId + " deleted successfully");
    }
}
