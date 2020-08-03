package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.viewhistory.DefaultViewHistoryService;
import com.es.phoneshop.model.viewhistory.ViewHistoryService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ProductDetailsPageServlet extends AbstractProductServlet {

    protected static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";
    private CartService cartService;
    private ViewHistoryService viewHistoryService;
    private ProductDao productDao;

    public ProductDetailsPageServlet() {
        super(PRODUCT_DETAILS_JSP);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        viewHistoryService = DefaultViewHistoryService.getInstance();
        productDao = ArrayListProductDao.getInstance();
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

        int quantity;
        try {
            quantity = getQuantity(request);
        } catch (ParseException e) {
            handleError(request, response, e);
            return;
        }

        try {
            cartService.add(cartService.getCart(request.getSession()), productId, quantity);
        } catch (OutOfStockException e) {
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

    private int getQuantity(HttpServletRequest request) throws ParseException {
        String quantityString = request.getParameter("quantity");
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        return numberFormat.parse(quantityString).intValue();
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }
}
