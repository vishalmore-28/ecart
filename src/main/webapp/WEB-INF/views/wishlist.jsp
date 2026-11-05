<c:set var="title" value="Wishlist"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<h1 class="page-title">My wishlist</h1>
<c:choose>
<c:when test="${empty products}"><div class="glass empty"><p>Nothing saved yet. Tap the heart on any product.</p><a class="btn" href="${ctx}/products">Browse products</a></div></c:when>
<c:otherwise>
<div class="grid">
  <c:forEach var="p" items="${products}">
    <div class="wl-item">
      <e:card p="${p}" wished="${true}"/>
      <c:if test="${p.stock > 0}">
        <form method="post" action="${ctx}/wishlist">
          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="tocart"><input type="hidden" name="pid" value="${p.id}">
          <button class="btn block sm" type="submit">Move to cart</button>
        </form>
      </c:if>
    </div>
  </c:forEach>
</div>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
