<c:set var="title" value="My orders"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<h1 class="page-title">My orders</h1>
<c:choose>
<c:when test="${empty orders}"><div class="glass empty"><p>You haven't placed any orders yet.</p><a class="btn" href="${ctx}/products">Start shopping</a></div></c:when>
<c:otherwise>
  <c:forEach var="o" items="${orders}">
    <a class="glass order-card" href="${ctx}/order?id=${o.id}">
      <div>
        <strong>#<c:out value="${o.orderNo}"/></strong>
        <div class="muted small"><fmt:formatDate value="${o.createdAt}" pattern="d MMM yyyy, h:mm a"/> &middot; ${o.itemCount} item<c:if test="${o.itemCount != 1}">s</c:if></div>
        <div class="muted small"><c:forEach var="i" items="${o.items}" end="1" varStatus="vs"><c:out value="${i.name}"/><c:if test="${!vs.last}">, </c:if></c:forEach><c:if test="${fn:length(o.items) > 2}"> +${fn:length(o.items) - 2} more</c:if></div>
      </div>
      <div class="right"><span class="st ${o.status}">${o.status}</span><div><strong><e:money v="${o.total}"/></strong></div></div>
    </a>
  </c:forEach>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
