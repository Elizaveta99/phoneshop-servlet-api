package com.es.phoneshop.web;

public class ProductDetailsPageServlet extends AbstractProductServlet {

    protected static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";

    public ProductDetailsPageServlet() {
        super(PRODUCT_DETAILS_JSP);
    }

}
