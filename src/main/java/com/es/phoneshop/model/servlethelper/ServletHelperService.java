package com.es.phoneshop.model.servlethelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public interface ServletHelperService {
    Map<Long, String> mapErrors(Long productId, Exception e);
    Long getProductIdIfExist(HttpServletRequest request, HttpServletResponse response, String productId) throws IOException;
    int getQuantity(String quantityString,  HttpServletRequest request) throws ParseException;
}
