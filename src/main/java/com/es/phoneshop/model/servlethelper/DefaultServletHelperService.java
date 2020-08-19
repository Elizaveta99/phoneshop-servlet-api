package com.es.phoneshop.model.servlethelper;

import com.es.phoneshop.exception.OutOfStockException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DefaultServletHelperService implements ServletHelperService {

    private DefaultServletHelperService() { }

    private static class SingletonHelper {
        private static final DefaultServletHelperService INSTANCE = new DefaultServletHelperService();
    }

    public static DefaultServletHelperService getInstance() {
        return DefaultServletHelperService.SingletonHelper.INSTANCE;
    }

    @Override
    public Map<Long, String> mapErrors(Long productId, Exception e) {
        Map<Long, String> errorAttributes = new HashMap<>();
        if (e.getClass().equals(ParseException.class)) {
            errorAttributes.put(productId, "Not a number");
        } else {
            if (((OutOfStockException) e).getStockRequested() <= 0) {
                errorAttributes.put(productId, "Can't be negative or zero");
            } else {
                errorAttributes.put(productId, "Out of stock, max available " + ((OutOfStockException) e).getStockAvailable());
            }
        }
        return errorAttributes;
    }

    @Override
    public Long getProductIdIfExist(HttpServletRequest request, HttpServletResponse response, String productId) throws IOException {
        Long id = null;
        try {
            id = Long.valueOf(productId);
        } catch (NumberFormatException ex) {
            request.setAttribute("message", "Product " + productId + " not found");
            response.sendError(404);
        }
        return id;
    }

    @Override
    public int getQuantity(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        return numberFormat.parse(quantityString).intValue();
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }
}
