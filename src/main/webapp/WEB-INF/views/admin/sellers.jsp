<c:set var="title" value="Sellers"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <h1 class="page-title">Sellers</h1>
  <nav class="tabs">
    <a class="${empty status ? 'cur' : ''}" href="${ctx}/admin/sellers">All</a>
    <c:forEach var="c" items="${counts}"><a class="${status == c.key ? 'cur' : ''}" href="${ctx}/admin/sellers?status=${c.key}">${c.key} (${c.value})</a></c:forEach>
  </nav>
  <section class="glass"><div class="table-wrap"><table>
    <tr><th>Shop</th><th>Owner</th><th>GSTIN</th><th>State</th><th>Applied</th><th>Status</th><th></th></tr>
    <c:forEach var="s" items="${sellers}">
      <tr><td><c:out value="${s.shopName}"/><div class="muted small"><c:out value="${s.businessType}"/></div></td>
        <td><c:out value="${s.ownerName}"/><div class="muted small"><c:out value="${s.email}"/></div></td>
        <td><c:out value="${s.gstRegistered ? s.gstin : 'Unregistered'}"/></td><td><c:out value="${s.state}"/></td>
        <td><fmt:formatDate value="${s.createdAt}" pattern="d MMM yyyy"/></td>
        <td><span class="st ${s.chip}">${s.status}</span></td>
        <td><a class="btn ghost sm" href="${ctx}/admin/sellers?id=${s.userId}">Review</a></td></tr>
    </c:forEach>
    <c:if test="${empty sellers}"><tr><td colspan="7" class="muted">No sellers here.</td></tr></c:if>
  </table></div></section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
