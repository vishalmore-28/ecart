<c:set var="title" value="Admin products"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title">Products</h1><a class="btn sm" href="${ctx}/admin/products?new=1">+ Add product</a></div>
  <form class="search-inline" method="get" action="${ctx}/admin/products"><input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search products"><button class="btn sm" type="submit">Search</button></form>
  <section class="glass"><div class="table-wrap"><table>
    <tr><th>Product</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th><th></th></tr>
    <c:forEach var="p" items="${products}">
      <tr class="${p.active ? '' : 'off-row'}">
        <td><c:out value="${p.icon}"/> <a href="${ctx}/product?id=${p.id}"><c:out value="${p.name}"/></a><div class="muted small"><c:out value="${p.brand}"/> &middot; Sold by <c:out value="${p.soldBy}"/> &middot; GST ${p.gstPercent}%</div></td>
        <td><c:out value="${p.categoryName}"/></td>
        <td><e:money v="${p.price}"/> <small class="muted"><s><e:money v="${p.mrp}"/></s></small></td>
        <td class="${p.stock <= 5 ? 'red' : ''}">${p.stock}</td>
        <td><span class="st ${p.active ? 'DELIVERED' : 'CANCELLED'}">${p.active ? 'Live' : 'Hidden'}</span></td>
        <td class="nowrap"><a class="btn ghost sm" href="${ctx}/admin/products?edit=${p.id}">Edit</a>
          <form method="post" action="${ctx}/admin/products" class="inline-form"><input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="toggle"><input type="hidden" name="id" value="${p.id}"><button class="btn ghost sm" type="submit">${p.active ? 'Hide' : 'Show'}</button></form></td>
      </tr>
    </c:forEach>
  </table></div></section>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
