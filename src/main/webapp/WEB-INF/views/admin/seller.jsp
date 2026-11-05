<c:set var="title" value="Review seller"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<div class="admin"><%@ include file="/WEB-INF/views/admin/_nav.jspf" %>
<div>
  <div class="sec-head"><h1 class="page-title"><c:out value="${s.shopName}"/></h1><span class="st ${s.chip}">${s.status}</span></div>
  <div class="cols">
    <div>
      <section class="glass form-card"><h2>Business &amp; GST</h2>
        <div class="sum-line"><span>Business type</span><strong><c:out value="${s.businessType}"/></strong></div>
        <div class="sum-line"><span>PAN</span><strong><c:out value="${s.pan}"/></strong></div>
        <div class="sum-line"><span>GSTIN</span><strong><c:out value="${s.gstRegistered ? s.gstin : 'Not registered (no GST can be charged)'}"/></strong></div>
        <div class="sum-line"><span>Pickup address</span><strong><c:out value="${s.fullAddress}"/></strong></div>
        <p class="muted small">The system already checked the GSTIN format and check digit, that the PAN inside the GSTIN equals the PAN entered, and that the state matches the GSTIN state code. Verify the registration on the official GST portal before approving.</p>
      </section>
      <section class="glass form-card"><h2>Owner &amp; bank</h2>
        <div class="sum-line"><span>Owner</span><strong><c:out value="${s.ownerName}"/></strong></div>
        <div class="sum-line"><span>Email</span><strong><c:out value="${s.email}"/></strong></div>
        <div class="sum-line"><span>Mobile</span><strong><c:out value="${s.phone}"/></strong></div>
        <div class="sum-line"><span>Bank</span><strong>XXXXXX<c:out value="${s.bankLast4}"/> &middot; <c:out value="${s.bankIfsc}"/> &middot; <c:out value="${s.bankHolder}"/></strong></div>
        <div class="sum-line"><span>Products</span><strong>${counts[1]} live / ${counts[0]} total</strong></div>
      </section>
    </div>
    <aside>
      <div class="glass summary"><h2>Decision</h2>
        <p class="muted small">Applied <fmt:formatDate value="${s.createdAt}" pattern="d MMM yyyy, h:mm a"/>
          <c:if test="${not empty s.reviewedAt}"><br>Last reviewed <fmt:formatDate value="${s.reviewedAt}" pattern="d MMM yyyy, h:mm a"/></c:if></p>
        <c:if test="${not empty s.reviewNote}"><p>Note: <c:out value="${s.reviewNote}"/></p></c:if>
        <form method="post" action="${ctx}/admin/sellers">
          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${s.userId}">
          <c:if test="${s.status == 'PENDING'}">
            <label>Reason (needed to reject)<input type="text" name="note" maxlength="250"></label>
            <button class="btn block" type="submit" name="action" value="approve">Approve shop</button>
            <button class="btn ghost block" type="submit" name="action" value="reject">Reject</button>
          </c:if>
          <c:if test="${s.status == 'APPROVED'}">
            <label>Reason (shown to the seller)<input type="text" name="note" maxlength="250"></label>
            <button class="btn ghost block" type="submit" name="action" value="suspend">Suspend shop</button>
          </c:if>
          <c:if test="${s.status == 'SUSPENDED'}"><button class="btn block" type="submit" name="action" value="reactivate">Reactivate shop</button></c:if>
          <c:if test="${s.status == 'REJECTED'}"><button class="btn block" type="submit" name="action" value="reopen">Re-open application</button></c:if>
        </form>
        <p class="muted small">Suspending a shop hides all its products from the store immediately.</p>
      </div>
      <p><a class="btn ghost block" href="${ctx}/admin/sellers">&larr; All sellers</a></p>
    </aside>
  </div>
</div></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
