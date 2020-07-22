package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    private ProductDao productDao;
    private final String QUERY_PRODUCT = "queryProduct";
    private final String SORT = "sort";
    private final String ORDER = "order";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryProduct = request.getParameter(QUERY_PRODUCT);
        String sortField = request.getParameter(SORT);
        String sortOrder = request.getParameter(ORDER);
        if (Objects.nonNull(sortField)) {
            sortField = sortField.toUpperCase();
        }
        if (Objects.nonNull(sortOrder)) {
            sortOrder = sortOrder.toUpperCase();
        }
        request.setAttribute("products",
                productDao.findProducts(queryProduct,
                        Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                        Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

}
