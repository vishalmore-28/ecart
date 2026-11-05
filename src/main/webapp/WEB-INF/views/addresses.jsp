<c:set var="title" value="Addresses"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<h1 class="page-title">Delivery addresses</h1>
<div class="cols even">
  <section>
    <c:if test="${empty addresses}"><div class="glass empty"><p>No saved addresses yet.</p></div></c:if>
    <c:forEach var="x" items="${addresses}">
      <div class="glass addr">
        <div><strong><c:out value="${x.fullName}"/></strong> <c:if test="${x.defaultAddress}"><span class="pill ok">Default</span></c:if></div>
        <div><c:out value="${x.line1}"/><c:if test="${not empty x.line2}">, <c:out value="${x.line2}"/></c:if></div>
        <div><c:out value="${x.city}"/>, <c:out value="${x.state}"/> &ndash; <c:out value="${x.pincode}"/></div>
        <div class="muted small">Phone: <c:out value="${x.phone}"/></div>
        <div class="row sm-gap">
          <a class="btn ghost sm" href="${ctx}/addresses?edit=${x.id}">Edit</a>
          <c:if test="${!x.defaultAddress}">
            <form method="post" action="${ctx}/addresses" class="inline-form"><input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="default"><input type="hidden" name="id" value="${x.id}"><button class="btn ghost sm" type="submit">Make default</button></form>
          </c:if>
          <form method="post" action="${ctx}/addresses" class="inline-form" data-confirm="Delete this address?"><input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${x.id}"><button class="link" type="submit">Delete</button></form>
        </div>
      </div>
    </c:forEach>
  </section>
  <section class="glass form-card">
    <h2>${empty a ? 'Add a new address' : 'Edit address'}</h2>
    <form method="post" action="${ctx}/addresses">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${empty a ? 0 : a.id}"><input type="hidden" name="next" value="<c:out value='${next}'/>">
      <label>Full name<input type="text" name="fullName" value="<c:out value='${a.fullName}'/>" required maxlength="100"></label>
      <label>Mobile number<input type="tel" name="phone" value="<c:out value='${a.phone}'/>" required pattern="[6-9][0-9]{9}" maxlength="10"></label>
      <label>House / street<input type="text" name="line1" value="<c:out value='${a.line1}'/>" required maxlength="200"></label>
      <label>Area / landmark (optional)<input type="text" name="line2" value="<c:out value='${a.line2}'/>" maxlength="200"></label>
      <div class="two">
        <label>City<input type="text" name="city" value="<c:out value='${a.city}'/>" required maxlength="80"></label>
        <label>State<input type="text" name="state" value="<c:out value='${a.state}'/>" required maxlength="80"></label>
      </div>
      <label>Pincode<input type="text" name="pincode" value="<c:out value='${a.pincode}'/>" required pattern="[0-9]{6}" maxlength="6" inputmode="numeric"></label>
      <button class="btn block" type="submit">Save address</button>
      <c:if test="${not empty a}"><a class="muted small center" href="${ctx}/addresses">Cancel editing</a></c:if>
    </form>
  </section>
</div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
