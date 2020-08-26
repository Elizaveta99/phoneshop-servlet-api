<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <br>
  <c:if test="${not empty param.message}">
    <div class="success">
        ${param.message}
    </div>
    <br>
  </c:if>
  <form action="${pageContext.servletContext.contextPath}/advancedSearch">
    <button>Advanced search</button>
  </form>
  <br>
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
        <td class="quantity">
          Quantity
        </td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc"></tags:sortLink>
          <tags:sortLink sort="price" order="desc"></tags:sortLink>
        </td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}" varStatus="status">
    <form method="post">
      <tr>
          <td>
            <img class="product-tile" src="${product.imageUrl}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
              ${product.description}
            </a>
          </td>
          <td class="quantity">
            <c:set var="error" value="${errors[product.id]}" />
            <input name="quantity" value="${not empty error ? param.quantity : 1}" class="quantity" />
            <c:if test="${not empty error}">
              <div class="error">
                  ${error}
              </div>
            </c:if>
            <input type="hidden" name="productId" value="${product.id}" />
          </td>
          <td class="price">
            <a href="${pageContext.servletContext.contextPath}/products/priceHistory/${product.id}">
              <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
            </a>
          </td>
          <td>
            <button>
              Add to cart
            </button>
          </td>
        </tr>
    </form>
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
