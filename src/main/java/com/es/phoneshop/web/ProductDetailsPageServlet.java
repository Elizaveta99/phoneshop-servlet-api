package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.viewhistory.DefaultViewHistoryService;
import com.es.phoneshop.model.viewhistory.ViewHistory;
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
        String productInfo = "";
        Product product;
        try {
            productInfo = request.getPathInfo().substring(1);
            Long id = Long.valueOf(productInfo);
            product = productDao.getProduct(id);
            request.setAttribute("product", product);
            Cart cart = cartService.getCart(request.getSession());
            request.setAttribute("cart", cart);
            viewHistoryService.addProductToViewHistory(request.getSession(), product);
            ViewHistory viewHistory = viewHistoryService.getViewHistory(request.getSession());
            request.setAttribute("viewHistory", viewHistory);
            request.getRequestDispatcher(PRODUCT_DETAILS_JSP).forward(request, response);
        } catch (ProductNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productInfo = request.getPathInfo().substring(1);
        Long productId = Long.valueOf(productInfo);
        Cart cart = cartService.getCart(request.getSession());
        request.setAttribute("cart", cart);
        ViewHistory viewHistory = viewHistoryService.getViewHistory(request.getSession());
        request.setAttribute("viewHistory", viewHistory);

        int quantity;
        try {
            quantity = getQuantity(request);
        } catch (ParseException e) {
            request.setAttribute("error", "Not a number");
            doGet(request, response);
            return;
        }

        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            if (e.getStockRequested() <= 0) {
                request.setAttribute("error", "Can't be negative or zero");
            } else {
                request.setAttribute("error", "Out of stock, max available " + e.getStockAvailable());
            }
            doGet(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/products?message=Product " + productId +" added to cart");
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
