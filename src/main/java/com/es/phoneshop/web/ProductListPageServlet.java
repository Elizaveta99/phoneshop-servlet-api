package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.servlethelper.DefaultServletHelperService;
import com.es.phoneshop.model.servlethelper.ServletHelperService;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.model.viewhistory.DefaultViewHistoryService;
import com.es.phoneshop.model.viewhistory.ViewHistory;
import com.es.phoneshop.model.viewhistory.ViewHistoryService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    protected static final String PRODUCTS_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    private final String QUERY_PRODUCT = "queryProduct";
    private final String SORT = "sort";
    private final String ORDER = "order";

    private ProductDao productDao;
    private CartService cartService;
    private ViewHistoryService viewHistoryService;
    private ServletHelperService servletHelperService;

    public ProductListPageServlet() {
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        viewHistoryService = DefaultViewHistoryService.getInstance();
        servletHelperService = DefaultServletHelperService.getInstance();
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
        ViewHistory viewHistory = viewHistoryService.getViewHistory(request.getSession());
        request.setAttribute("viewHistory", viewHistory);
        request.setAttribute("products",
                productDao.findProducts(queryProduct,
                        Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                        Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)));
        request.getRequestDispatcher(PRODUCTS_LIST_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = servletHelperService.getProductIdIfExist(request, response, request.getParameter("productId"));

        try {
            int quantity = servletHelperService.getQuantity(request.getParameter("quantity"), request);
            cartService.add(cartService.getCart(request.getSession()), productId, quantity);
        } catch (ParseException | OutOfStockException e) {
            handleError(productId, request, response, e);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/products?"
                + getQueryString(request) + "message=Product " + productId +" added to cart");
    }

    private String getQueryString(HttpServletRequest request) {
        return (request.getQueryString() != null) ? request.getQueryString() + "&" : "";
    }

    private void handleError(Long productId, HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        Map<Long, String> errorAttributes = servletHelperService.mapErrors(productId, e);
        request.setAttribute("errors", errorAttributes);
        doGet(request, response);
    }
}
