package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.ProductNotFoundException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractProductServlet extends HttpServlet {

    private ProductDao productDao;

    private final String jspPath;

    public AbstractProductServlet(String jspPath) {
        this.jspPath = jspPath;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productInfo = "";
        try {
            productInfo = request.getPathInfo().substring(1);
            Long id = Long.valueOf(productInfo);
            request.setAttribute("product", productDao.getProduct(id));
            request.getRequestDispatcher(jspPath).forward(request, response);
        } catch (ProductNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
    }

}
