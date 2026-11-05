<c:set var="title" value="My products"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/seller/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title">My products</h1><a class="btn sm" href="${ctx}/seller/products?new=1">+ Add product</a></div>
  <form class="search-inline" method="get" action="${ctx}/seller/products"><input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search my products"><button class="btn sm" type="submit">Search</button></form>
  <section class="glass"><div class="table-wrap"><table>
    <tr><th>Product</th><th>Price</th><th>GST</th><th>Stock</th><th>Status</th><th></th></tr>
    <c:forEach var="p" items="${products}">
      <tr class="${p.active ? '' : 'off-row'}">
        <td><c:out value="${p.icon}"/> <a href="${ctx}/product?id=${p.id}"><c:out value="${p.name}"/></a><div class="muted small"><c:out value="${p.brand}"/> &middot; <c:out value="${p.categoryName}"/></div></td>
        <td><e:money v="${p.price}"/> <small class="muted"><s><e:money v="${p.mrp}"/></s></small></td>
        <td>${p.gstPercent}%<c:if test="${not empty p.hsnCode}"><div class="muted small">HSN <c:out value="${p.hsnCode}"/></div></c:if></td>
        <td class="${p.stock <= 5 ? 'red' : ''}">${p.stock}</td>
        <td><span class="st ${p.active ? 'DELIVERED' : 'CANCELLED'}">${p.active ? 'Live' : 'Hidden'}</span></td>
        <td class="nowrap"><a class="btn ghost sm" href="${ctx}/seller/products?edit=${p.id}">Edit</a>
          <form method="post" action="${ctx}/seller/products" class="inline-form"><input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="toggle"><input type="hidden" name="id" value="${p.id}"><button class="btn ghost sm" type="submit">${p.active ? 'Hide' : 'Show'}</button></form></td>
      </tr>
    </c:forEach>
    <c:if test="${empty products}"><tr><td colspan="6" class="muted">You have not added any products yet.</td></tr></c:if>
  </table></div></section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
