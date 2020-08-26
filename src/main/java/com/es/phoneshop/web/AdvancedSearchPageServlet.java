package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class AdvancedSearchPageServlet extends HttpServlet {

    protected static final String ADVANCED_SEARCH_PAGE = "/WEB-INF/pages/advancedSearch.jsp";

    private ProductDao productDao;

    public AdvancedSearchPageServlet() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("products",new ArrayList<>());
        request.getRequestDispatcher(ADVANCED_SEARCH_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errorAttributes = new HashMap<>();

        BigDecimal minPrice = getPrice(request,"minPrice", errorAttributes, request.getParameter("minPrice").isEmpty() ? "0" : request.getParameter("minPrice"));
        BigDecimal maxPrice = getPrice(request,"maxPrice", errorAttributes, request.getParameter("maxPrice").isEmpty() ? "1000" : request.getParameter("maxPrice"));
        int minStock = getStock(request,"minStock", errorAttributes, request.getParameter("minStock").isEmpty() ? "0" : request.getParameter("minStock"));

        if (!errorAttributes.isEmpty()) {
            request.setAttribute("errors", errorAttributes);
            request.setAttribute("message", "Error occured while filling fields");
            doGet(request, response);
            return;
        }

        List<Product> foundProducts = productDao.advancedFindProduct(request.getParameter("productCode"), minPrice, maxPrice, minStock);
        request.setAttribute("products", foundProducts);
        request.setAttribute("message", "Found " + foundProducts.size() + " products");
        request.getRequestDispatcher(ADVANCED_SEARCH_PAGE).forward(request, response);
    }

    private BigDecimal getPrice(HttpServletRequest request, String requestParameter, Map<String, String> errorAttributes, String parameter) {
        try {
            return BigDecimal.valueOf(getPriceParse(parameter, request));
        } catch (ParseException e) {
            errorAttributes.put(requestParameter, "Not a number");
        } catch (IllegalArgumentException e) {
            errorAttributes.put(requestParameter, "Can't be negative");
        }
        return null;
    }

    private int getStock(HttpServletRequest request, String requestParameter, Map<String, String> errorAttributes, String parameter) {
        try {
            return getStockParse(parameter, request);
        } catch (ParseException e) {
            errorAttributes.put(requestParameter, "Not a number");
        } catch (IllegalArgumentException e) {
            errorAttributes.put(requestParameter, "Can't be negative");
        }
        return 0;
    }

    private double getPriceParse(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        double price = numberFormat.parse(quantityString).doubleValue();
        if (price < 0) {
            throw new IllegalArgumentException();
        }
        return price;
    }

    private int getStockParse(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        int stock = numberFormat.parse(quantityString).intValue();
        if (stock < 0) {
            throw new IllegalArgumentException();
        }
        return stock;
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }
}
