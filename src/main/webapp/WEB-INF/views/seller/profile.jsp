<c:set var="title" value="Shop profile"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/seller/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title">Shop profile</h1><span class="st ${shop.chip}">${shop.status}</span></div>
  <div class="cols even">
    <section class="glass form-card"><h2>Business</h2>
      <div class="sum-line"><span>Shop name</span><strong><c:out value="${shop.shopName}"/></strong></div>
      <div class="sum-line"><span>Business type</span><strong><c:out value="${shop.businessType}"/></strong></div>
      <div class="sum-line"><span>PAN</span><strong><c:out value="${shop.pan}"/></strong></div>
      <div class="sum-line"><span>GSTIN</span><strong><c:out value="${shop.gstRegistered ? shop.gstin : 'Not registered'}"/></strong></div>
      <div class="sum-line"><span>Pickup address</span><strong><c:out value="${shop.fullAddress}"/></strong></div>
    </section>
    <section class="glass form-card"><h2>Owner &amp; bank</h2>
      <div class="sum-line"><span>Owner</span><strong><c:out value="${shop.ownerName}"/></strong></div>
      <div class="sum-line"><span>Email</span><strong><c:out value="${shop.email}"/></strong></div>
      <div class="sum-line"><span>Mobile</span><strong><c:out value="${shop.phone}"/></strong></div>
      <div class="sum-line"><span>Bank account</span><strong>XXXXXX<c:out value="${shop.bankLast4}"/> &middot; <c:out value="${shop.bankIfsc}"/></strong></div>
      <div class="sum-line"><span>Account holder</span><strong><c:out value="${shop.bankHolder}"/></strong></div>
    </section>
  </div>
  <p class="muted small">To change GSTIN, PAN or bank details, contact ecart support &mdash; these are verified by the ecart team.</p>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
