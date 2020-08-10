package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public abstract class AbstractProductServlet extends HttpServlet {

    protected ProductDao productDao;
    protected CartService cartService;
    private final String jspPath;

    public AbstractProductServlet(String jspPath) {
        this.jspPath = jspPath;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productInfo = "";
        try {
            productInfo = request.getPathInfo().substring(1);
            Long id = Long.valueOf(productInfo);
            request.setAttribute("product", productDao.getProduct(id));
            request.setAttribute("cart", cartService.getCart(request.getSession()));
            request.getRequestDispatcher(jspPath).forward(request, response);
        } catch (ProductNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
    }

    protected Long getProductIdIfExist(HttpServletRequest request, HttpServletResponse response, String productId) throws IOException {
        Long id = null;
        try {
            id = Long.valueOf(productId);
        } catch (NumberFormatException ex) {
            request.setAttribute("message", "Product " + productId + " not found");
            response.sendError(404);
        }
        return id;
    }

    protected int getQuantity(String quantityString,  HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        return numberFormat.parse(quantityString).intValue();
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }

}
