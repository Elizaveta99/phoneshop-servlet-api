<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request" />
<tags:master pageTitle="Product Details">
    <br>
    <c:if test="${not empty error}">
        <div class="error">
                Error occurred while adding to cart
        </div>
    </c:if>
    <p>
        ${product.description}
    </p>
    <form method="post">
        <table>
            <tr>
                <td>Image</td>
                <td><img src="${product.imageUrl}"></td>
            </tr>
            <tr>
                <td>code</td>
                <td>${product.code}</td>
            </tr>
            <tr>
                <td>stock</td>
                <td>${product.stock}</td>
            </tr>
            <tr>
                <td>price</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>quantity</td>
                <td>
                    <input name="quantity" value="${not empty error ? param.quantity : 1}" class="quantity"/>
                    <c:if test="${not empty error}">
                        <div class="error">
                            ${error}
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>
        <button>Add to cart</button>
    </form>
    <p>
        <b>Recently viewed</b>
    </p>
    <table>
        <tr>
            <c:forEach var="product" items="${viewHistory.getLastViewedProducts()}">
                <td>
                    <div>
                        <img class="product-tile" src="${product.imageUrl}">
                    </div>
                    <div>
                            ${product.description}
                    </div>
                    <div>
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                    </div>
                </td>
            </c:forEach>
        </tr>
    </table>
</tags:master>