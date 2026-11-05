<%@ tag body-content="empty" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="e" tagdir="/WEB-INF/tags" %>
<%@ attribute name="p" required="true" type="com.ecart.model.Product" %>
<%@ attribute name="wished" required="false" type="java.lang.Boolean" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<article class="glass product">
  <a class="thumb" href="${ctx}/product?id=${p.id}">
    <c:choose>
      <c:when test="${not empty p.imageUrl}"><img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>" loading="lazy"></c:when>
      <c:otherwise><span class="ph"><c:out value="${p.icon}"/></span></c:otherwise>
    </c:choose>
    <c:if test="${p.discountPercent > 0}"><span class="badge off">${p.discountPercent}% off</span></c:if>
    <c:if test="${p.stock == 0}"><span class="badge out">Out of stock</span></c:if>
  </a>
  <form class="heart" method="post" action="${ctx}/wishlist">
    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="pid" value="${p.id}">
    <button type="submit" class="${wished ? 'on' : ''}" title="Wishlist" aria-label="Toggle wishlist">${wished ? '&#9829;' : '&#9825;'}</button>
  </form>
  <div class="body">
    <div class="brand"><c:out value="${p.brand}"/></div>
    <a class="title" href="${ctx}/product?id=${p.id}"><c:out value="${p.name}"/></a>
    <div class="rate"><c:if test="${p.reviewCount > 0}"><e:stars n="${p.roundedRating}"/> <small>(${p.reviewCount})</small></c:if></div>
    <div class="price"><strong><e:money v="${p.price}"/></strong> <c:if test="${p.discountPercent > 0}"><s><e:money v="${p.mrp}"/></s></c:if></div>
  </div>
</article>
