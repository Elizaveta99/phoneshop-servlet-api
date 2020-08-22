package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.servlethelper.DefaultServletHelperService;
import com.es.phoneshop.model.servlethelper.ServletHelperService;
import com.es.phoneshop.model.viewhistory.DefaultViewHistoryService;
import com.es.phoneshop.model.viewhistory.ViewHistoryService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class ProductDetailsPageServlet extends HttpServlet {

    protected static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";
    private ProductDao productDao;
    private CartService cartService;
    private ViewHistoryService viewHistoryService;
    private ServletHelperService servletHelperService;

    public ProductDetailsPageServlet() {
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        viewHistoryService = DefaultViewHistoryService.getInstance();
        servletHelperService = DefaultServletHelperService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Product product = getProductIfExist(request, response);
        setAttributes(request, product);
        request.getRequestDispatcher(PRODUCT_DETAILS_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = getProductIfExist(request, response).getId();

        try {
            int quantity = servletHelperService.getQuantity(request.getParameter("quantity"), request);
            cartService.add(cartService.getCart(request.getSession()), productId, quantity);
        } catch (ParseException | OutOfStockException e) {
            handleError(request, response, e);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/products?message=Product " + productId +" added to cart");
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        if (e.getClass().equals(ParseException.class)) {
            request.setAttribute("error", "Not a number");
        } else {
            if (((OutOfStockException) e).getStockRequested() <= 0) {
                request.setAttribute("error", "Can't be negative or zero");
            } else {
                request.setAttribute("error", "Out of stock, max available " + ((OutOfStockException) e).getStockAvailable());
            }
        }
        doGet(request, response);
    }
    
    private Product getProductIfExist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productInfo = "";
        Product product = null;
        try {
            productInfo = request.getPathInfo().substring(1);
            Long id = Long.valueOf(productInfo);
            product = productDao.getItem(id);
        } catch (ItemNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
        return product;
    }

    private void setAttributes(HttpServletRequest request, Product product) {
        request.setAttribute("product", product);
        viewHistoryService.addProductToViewHistory(request.getSession(), product);
        request.setAttribute("viewHistory", viewHistoryService.getViewHistory(request.getSession()));
    }
}
