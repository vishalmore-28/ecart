<c:set var="title" value="Checkout"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<h1 class="page-title">Checkout</h1>
<form method="post" action="${ctx}/checkout" class="cols" autocomplete="off">
  <input type="hidden" name="_csrf" value="${csrf}">
  <div>
    <section class="glass form-card">
      <h2>1. Delivery address</h2>
      <c:forEach var="x" items="${addresses}">
        <label class="opt"><input type="radio" name="addressId" value="${x.id}" ${x.defaultAddress ? 'checked' : ''} required>
          <span><strong><c:out value="${x.fullName}"/></strong><br><c:out value="${x.line1}"/><c:if test="${not empty x.line2}">, <c:out value="${x.line2}"/></c:if>, <c:out value="${x.city}"/>, <c:out value="${x.state}"/> &ndash; <c:out value="${x.pincode}"/><br><small class="muted">Phone: <c:out value="${x.phone}"/></small></span>
        </label>
      </c:forEach>
      <a class="muted small" href="${ctx}/addresses?next=/checkout">+ Add or edit addresses</a>
    </section>

    <section class="glass form-card">
      <h2>2. Payment</h2>
      <p class="muted small">Demo gateway &mdash; use any valid-looking details; no real money moves. Card numbers are never stored.</p>
      <c:if test="${codAllowed}"><label class="opt"><input type="radio" name="method" value="COD" checked><span><strong>Cash on delivery</strong><br><small class="muted">Pay when your order arrives</small></span></label></c:if>
      <label class="opt"><input type="radio" name="method" value="UPI" ${codAllowed ? '' : 'checked'}><span><strong>UPI</strong><br><small class="muted">Pay with any UPI app</small></span></label>
      <div class="pay-box" data-pay="UPI"><label>UPI ID<input type="text" name="upi" placeholder="name@bank" maxlength="60"></label></div>
      <label class="opt"><input type="radio" name="method" value="CARD"><span><strong>Credit / Debit card</strong><br><small class="muted">Test card: 4111 1111 1111 1111</small></span></label>
      <div class="pay-box" data-pay="CARD">
        <label>Card number<input type="text" name="cardNumber" inputmode="numeric" maxlength="23" placeholder="1234 5678 9012 3456" autocomplete="off"></label>
        <label>Name on card<input type="text" name="cardName" maxlength="60" autocomplete="off"></label>
        <div class="two">
          <label>Expiry (MM/YY)<input type="text" name="cardExpiry" maxlength="5" placeholder="08/29" autocomplete="off"></label>
          <label>CVV<input type="password" name="cardCvv" maxlength="4" inputmode="numeric" autocomplete="off"></label>
        </div>
      </div>
      <c:if test="${!codAllowed}"><p class="muted small">Cash on delivery is unavailable for orders above &#8377;50,000.</p></c:if>
    </section>
  </div>

  <aside class="glass summary">
    <h2>Order summary</h2>
    <c:forEach var="it" items="${items}">
      <div class="sum-line"><span><c:out value="${it.product.name}"/> &times; ${it.qty}</span><span><e:money v="${it.lineTotal}"/></span></div>
    </c:forEach>
    <dl class="totals">
      <dt>Subtotal</dt><dd><e:money v="${t.subtotal}"/></dd>
      <c:if test="${t.discount.signum() > 0}"><dt>Discount (<c:out value="${t.couponCode}"/>)</dt><dd class="green">&minus; <e:money v="${t.discount}"/></dd></c:if>
      <dt>GST (18%)</dt><dd><e:money v="${t.tax}"/></dd>
      <dt>Shipping</dt><dd><c:choose><c:when test="${t.shipping.signum() == 0}">Free</c:when><c:otherwise><e:money v="${t.shipping}"/></c:otherwise></c:choose></dd>
      <dt class="grand">Total</dt><dd class="grand"><e:money v="${t.total}"/></dd>
    </dl>
    <button class="btn block" type="submit">Place order</button>
    <a class="muted small center" href="${ctx}/cart">&larr; Back to cart</a>
  </aside>
</form>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
