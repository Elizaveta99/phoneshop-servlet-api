package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutPageServlet extends HttpServlet {

    protected static final String CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";
    private CartService cartService;
    private OrderService orderService;

    public CheckoutPageServlet() {
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showOrder(request, response, orderService.getOrder(cartService.getCart(request.getSession())));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        Order order = orderService.getOrder(cart);
        Map<String, String> errorAttributes = new HashMap<>();

        setRequiredParameter(request, "firstName", errorAttributes, order::setFirstName);
        setRequiredParameter(request, "lastName", errorAttributes, order::setLastName);
        setRequiredPhoneParameter(request, errorAttributes, order);
        setRequiredParameter(request, "deliveryAddress", errorAttributes, order::setDeliveryAddress);
        setRequiredDateParameter(request, errorAttributes, order);
        setPaymentMethod(request, errorAttributes, order);

        handleErrors(request, response, errorAttributes, order);
    }

    private boolean isNotEmpty(String parameter, Map<String, String> errorAttributes, String value) {
        if (StringUtils.isBlank(value)) {
            errorAttributes.put(parameter, "Value is required");
            return false;
        }
        return true;
    }

    private void setRequiredPhoneParameter(HttpServletRequest request,
                                           Map<String, String> errorAttributes, Order order) {
        String value = request.getParameter("phone");
        if (isNotEmpty("phone", errorAttributes, value)) {
            String phonePattern = "|^(\\+\\d{1,3}( )?)?(\\d{2}[ ]?)(\\d{3}[- ]?)(\\d{2}[- ]?)\\d{2}$";
            Pattern pattern = Pattern.compile(phonePattern);
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                order.setPhone(value);
            } else {
                errorAttributes.put("phone", "Invalid phone number");
            }
        }
    }

    private void setRequiredParameter(HttpServletRequest request, String parameter,
                                      Map<String, String> errorAttributes, Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (isNotEmpty(parameter, errorAttributes, value))
            consumer.accept(value);
    }

    private void setRequiredDateParameter(HttpServletRequest request,
                                          Map<String, String> errorAttributes, Order order) {
        String value = request.getParameter("deliveryDate");
        if (isNotEmpty("deliveryDate", errorAttributes, value))
        {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date dateValue = null;
            try {
                dateValue = format.parse(value);
            } catch (ParseException e) {
                errorAttributes.put("deliveryDate", "Wrong format, should be: dd-MM-yyyy");
            }
            order.setDeliveryDate(dateValue);
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errorAttributes, Order order) {
        String value = request.getParameter("paymentMethod");
        if (isNotEmpty("paymentMethod", errorAttributes, value))
        {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    private void handleErrors(HttpServletRequest request, HttpServletResponse response,
                              Map<String, String> errorAttributes, Order order) throws IOException, ServletException {
        if (errorAttributes.isEmpty()) {
            orderService.placeOrder(order);
            response.sendRedirect(request.getContextPath() + "/overview/" + order.getId());
        } else {
            request.setAttribute("errors", errorAttributes);
            showOrder(request, response, order);
        }
    }

    private void showOrder(HttpServletRequest request, HttpServletResponse response, Order order) throws ServletException, IOException {
        request.setAttribute("order", order);
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher(CHECKOUT_JSP).forward(request, response);
    }
}
