package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.ItemNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductPriceHistoryPageServlet extends HttpServlet {

    protected static final String PRODUCT_PRICE_HISTORY_JSP = "/WEB-INF/pages/productPriceHistory.jsp";
    private ProductDao productDao;

    public ProductPriceHistoryPageServlet() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productInfo = "";
        try {
            productInfo = request.getPathInfo().substring(1);
            Long id = Long.valueOf(productInfo);
            request.setAttribute("product", productDao.getItem(id));
            request.getRequestDispatcher(PRODUCT_PRICE_HISTORY_JSP).forward(request, response);
        } catch (ItemNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
    }
}
