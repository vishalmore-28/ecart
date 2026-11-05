<c:set var="title" value="${empty p ? 'Add product' : 'Edit product'}"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<section class="glass form-card wide">
  <h1 class="page-title">${empty p ? 'Add product' : 'Edit product'}</h1>
  <form method="post" action="${ctx}/admin/products">
    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${empty p ? 0 : p.id}">
    <label>Name<input type="text" name="name" value="<c:out value='${p.name}'/>" required maxlength="200"></label>
    <div class="two">
      <label>Brand<input type="text" name="brand" value="<c:out value='${p.brand}'/>" maxlength="80"></label>
      <label>Category
        <select name="categoryId"><c:forEach var="k" items="${categories}"><option value="${k.id}" ${p.categoryId == k.id ? 'selected' : ''}><c:out value="${k.icon}"/> <c:out value="${k.name}"/></option></c:forEach></select>
      </label>
    </div>
    <div class="two">
      <label>Or create a new category<input type="text" name="newCategory" maxlength="80" placeholder="Leave blank to use the one above"></label>
      <label>New category icon (emoji)<input type="text" name="newIcon" maxlength="4"></label>
    </div>
    <label>Description<textarea name="description" rows="4"><c:out value="${p.description}"/></textarea></label>
    <div class="three">
      <label>Selling price (&#8377;)<input type="number" name="price" step="0.01" min="1" value="${p.price}" required></label>
      <label>MRP (&#8377;)<input type="number" name="mrp" step="0.01" min="1" value="${p.mrp}" required></label>
      <label>Stock<input type="number" name="stock" min="0" value="${empty p ? 10 : p.stock}" required></label>
    </div>
    <div class="two">
      <label>GST rate
        <select name="gst"><c:forEach var="g" items="${[0,5,12,18,28]}"><option value="${g}" ${(empty p ? 18 : p.gstPercent) == g ? 'selected' : ''}>${g}%</option></c:forEach></select>
      </label>
      <label>HSN code (4, 6 or 8 digits, optional)<input type="text" name="hsn" value="<c:out value='${p.hsnCode}'/>" maxlength="8" pattern="[0-9]{4}([0-9]{2}([0-9]{2})?)?"></label>
    </div>
    <label>Image URL (https, optional)<input type="url" name="imageUrl" value="<c:out value='${p.imageUrl}'/>" maxlength="500"></label>
    <label class="check"><input type="checkbox" name="active" ${empty p || p.active ? 'checked' : ''}> Visible in store</label>
    <div class="row"><button class="btn" type="submit">Save product</button><a class="btn ghost" href="${ctx}/admin/products">Cancel</a></div>
  </form>
</section></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
