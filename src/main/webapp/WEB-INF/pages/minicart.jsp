<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<fmt:formatNumber var="fmtCost" value="${cart.totalCost}" type="currency" currencySymbol="${cart.currency}" />
<div class="minicart">
  <a href="${pageContext.servletContext.contextPath}/cart">
    Cart:
  </a>
  quantity - ${not empty cart.totalQuantity ? cart.totalQuantity : 0}, cost - ${not empty fmtCost ? fmtCost : 0}
</div>
