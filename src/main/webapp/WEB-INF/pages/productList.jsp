<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <p>
    Cart: ${cart}
  </p>
  <c:if test="${not empty param.message}">
    <div class="success">
        ${param.message}
    </div>
    <br>
  </c:if>
  <form>
    <input type="text" name="queryProduct" value="${param.queryProduct}" placeholder="Search product...">
    <button>Search</button>
  </form>
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
          Description
          <tags:sortLink sort="description" order="asc"></tags:sortLink>
          <tags:sortLink sort="description" order="desc"></tags:sortLink>
        </td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc"></tags:sortLink>
          <tags:sortLink sort="price" order="desc"></tags:sortLink>
        </td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
          <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
            ${product.description}
          </a>
        </td>
        <td class="price">
          <a href="${pageContext.servletContext.contextPath}/products/priceHistory/${product.id}">
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
          </a>
        </td>
      </tr>
    </c:forEach>
  </table>
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
