package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.model.viewhistory.DefaultViewHistoryService;
import com.es.phoneshop.model.viewhistory.ViewHistory;
import com.es.phoneshop.model.viewhistory.ViewHistoryService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ProductListPageServlet extends AbstractProductServlet {

    protected static final String PRODUCTS_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    private ViewHistoryService viewHistoryService;
    private final String QUERY_PRODUCT = "queryProduct";
    private final String SORT = "sort";
    private final String ORDER = "order";

    public ProductListPageServlet() {
        super(PRODUCTS_LIST_JSP);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        viewHistoryService = DefaultViewHistoryService.getInstance();
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
        request.setAttribute("cart", cartService.getCart(request.getSession()));
        request.setAttribute("products",
                productDao.findProducts(queryProduct,
                        Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                        Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)));
        request.getRequestDispatcher(PRODUCTS_LIST_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = getProductIdIfExist(request, response, request.getParameter("productId"));
        Map<Long, String> errorAttributes = new HashMap<>();

        try {
            int quantity = getQuantity(request.getParameter("quantity"), request);
            cartService.add(cartService.getCart(request.getSession()), productId, quantity);
        } catch (ParseException | OutOfStockException e) {
            handleError(errorAttributes, productId, request, response, e);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/products?"
                + getQueryString(request) + "message=Product " + productId +" added to cart");
    }

    private String getQueryString(HttpServletRequest request) {
        return !(request.getQueryString() == null) ? request.getQueryString() + "&" : "";
    }

    private void handleError(Map<Long, String> errorAttributes, Long productId, HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        if (e.getClass().equals(ParseException.class)) {
            errorAttributes.put(productId, "Not a number");
        } else {
            if (((OutOfStockException) e).getStockRequested() <= 0) {
                errorAttributes.put(productId, "Can't be negative or zero");
            } else {
                errorAttributes.put(productId, "Out of stock, max available " + ((OutOfStockException) e).getStockAvailable());
            }
        }
        request.setAttribute("errors", errorAttributes);
        doGet(request, response);
    }
}
