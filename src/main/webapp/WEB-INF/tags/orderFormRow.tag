<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@attribute name="name" required="true" %>
<%@attribute name="label" required="true" %>
<%@attribute name="order" required="true" type="com.es.phoneshop.model.order.Order" %>
<%@attribute name="errors" required="true" type="java.util.Map" %>

<tr>
    <td>${label}<span style="color: red">*</span></td>
    <td>
        <c:set var="error" value="${errors[name]}" />
        <c:set var="orderName" value="${order[name]}" />
        <c:if test="${name == 'deliveryDate'}">
            <fmt:formatDate pattern="dd-MM-yyyy" value="${order[name]}" var="orderName" />
        </c:if>
        <input name="${name}" value="${not empty error ? param[name] : orderName}" />
        <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
        </c:if>
    </td>
</tr>