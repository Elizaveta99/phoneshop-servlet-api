<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@attribute name="name" required="true" %>
<%@attribute name="label" required="true" %>
<%@attribute name="order" required="true" type="com.es.phoneshop.model.order.Order" %>

<tr>
    <td>${label}</td>
    <td>
        <c:set var="orderName" value="${order[name]}" />
        <c:if test="${name == 'deliveryDate'}">
            <fmt:formatDate pattern="dd-MM-yyyy" value="${order[name]}" var="orderName" />
        </c:if>
        ${orderName}
    </td>
</tr>