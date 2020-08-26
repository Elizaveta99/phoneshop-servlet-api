<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@attribute name="name" required="true" %>
<%@attribute name="label" required="true" %>
<%@attribute name="errors" required="true" type="java.util.Map" %>

<tr>
    <td>${label}</td>
    <td>
        <c:set var="error" value="${errors[name]}" />
        <input name="${name}" value="${param[name]}" />
        <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
        </c:if>
    </td>
</tr>