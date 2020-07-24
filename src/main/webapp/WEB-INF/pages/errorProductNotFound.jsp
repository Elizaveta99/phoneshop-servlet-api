<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="bodyContent">
    <% if (StringUtils.isBlank((CharSequence) request.getAttribute("message"))) {
            out.println("Page not found");
        } else {
            out.println(request.getAttribute("message"));
        }
    %>
</c:set>
<tags:master pageTitle="Product not found">
    <h1> ${bodyContent} </h1>
</tags:master>