<c:set var="title" value="Customers"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <h1 class="page-title">Customers</h1>
  <section class="glass"><div class="table-wrap"><table>
    <tr><th>Name</th><th>Email</th><th>Role</th><th>Joined</th><th>Status</th><th></th></tr>
    <c:forEach var="u" items="${users}">
      <tr><td><c:out value="${u.name}"/></td><td><c:out value="${u.email}"/></td><td>${u.role}</td>
        <td><fmt:formatDate value="${u.createdAt}" pattern="d MMM yyyy"/></td>
        <td><span class="st ${u.active ? 'DELIVERED' : 'CANCELLED'}">${u.active ? 'Active' : 'Disabled'}</span></td>
        <td><c:if test="${!u.admin}"><form method="post" action="${ctx}/admin/users" class="inline-form" data-confirm="${u.active ? 'Disable' : 'Enable'} this account?"><input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${u.id}"><button class="btn ghost sm" type="submit">${u.active ? 'Disable' : 'Enable'}</button></form></c:if></td></tr>
    </c:forEach>
  </table></div></section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
