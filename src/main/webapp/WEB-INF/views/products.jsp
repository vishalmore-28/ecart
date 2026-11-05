<c:set var="title" value="Shop"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="shop">
  <aside class="glass filters">
    <form method="get" action="${ctx}/products">
      <label>Search<input type="search" name="q" value="<c:out value='${f.q}'/>" maxlength="80"></label>
      <label>Category
        <select name="cat"><option value="0">All categories</option>
          <c:forEach var="k" items="${navCategories}"><option value="${k.id}" ${f.category == k.id ? 'selected' : ''}><c:out value="${k.name}"/></option></c:forEach>
        </select>
      </label>
      <div class="two">
        <label>Min &#8377;<input type="number" name="min" min="0" value="${f.min}"></label>
        <label>Max &#8377;<input type="number" name="max" min="0" value="${f.max}"></label>
      </div>
      <label>Sort by
        <select name="sort">
          <option value="new" ${f.sort == 'new' ? 'selected' : ''}>Newest</option>
          <option value="priceasc" ${f.sort == 'priceasc' ? 'selected' : ''}>Price: low to high</option>
          <option value="pricedesc" ${f.sort == 'pricedesc' ? 'selected' : ''}>Price: high to low</option>
          <option value="discount" ${f.sort == 'discount' ? 'selected' : ''}>Biggest discount</option>
          <option value="rating" ${f.sort == 'rating' ? 'selected' : ''}>Top rated</option>
        </select>
      </label>
      <label class="check"><input type="checkbox" name="instock" value="1" ${f.inStock ? 'checked' : ''}> In stock only</label>
      <button class="btn block" type="submit">Apply filters</button>
      <a class="muted small center" href="${ctx}/products">Reset</a>
    </form>
  </aside>
  <section>
    <div class="sec-head"><h2>${total} product<c:if test="${total != 1}">s</c:if></h2></div>
    <c:choose>
      <c:when test="${empty products}"><div class="glass empty"><p>No products match your filters.</p><a class="btn" href="${ctx}/products">Clear filters</a></div></c:when>
      <c:otherwise><div class="grid"><c:forEach var="p" items="${products}"><e:card p="${p}" wished="${wished.contains(p.id)}"/></c:forEach></div></c:otherwise>
    </c:choose>
    <c:if test="${pages > 1}">
      <nav class="pager">
        <c:forEach begin="1" end="${pages}" var="i">
          <a class="${i == f.page ? 'cur' : ''}" href="${ctx}/products?${empty qs ? '' : qs.concat('&amp;')}page=${i}">${i}</a>
        </c:forEach>
      </nav>
    </c:if>
  </section>
</div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
