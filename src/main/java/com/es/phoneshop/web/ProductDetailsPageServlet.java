package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.viewhistory.DefaultViewHistoryService;
import com.es.phoneshop.model.viewhistory.ViewHistoryService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class ProductDetailsPageServlet extends AbstractProductServlet {

    protected static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";
    private ViewHistoryService viewHistoryService;

    public ProductDetailsPageServlet() {
        super(PRODUCT_DETAILS_JSP);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        viewHistoryService = DefaultViewHistoryService.getInstance();
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
            int quantity = getQuantity(request.getParameter("quantity"), request);
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
            product = productDao.getProduct(id);
        } catch (ProductNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
        return product;
    }

    private void setAttributes(HttpServletRequest request, Product product) {
        request.setAttribute("product", product);
        request.setAttribute("cart", cartService.getCart(request.getSession()));
        viewHistoryService.addProductToViewHistory(request.getSession(), product);
        request.setAttribute("viewHistory", viewHistoryService.getViewHistory(request.getSession()));
    }
}
