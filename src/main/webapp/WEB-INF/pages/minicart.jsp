<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<fmt:formatNumber var="fmtCost" value="${cart.totalCost}" type="currency" currencySymbol="${cart.currency}" />
<div class="minicart">
  Cart: quantity - ${not empty cart.totalQuantity ? cart.totalQuantity : 0}, cost - ${not empty fmtCost ? fmtCost : 0}
</div>
