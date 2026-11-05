<c:set var="title" value="Your cart"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<h1 class="page-title">Shopping cart</h1>
<c:choose>
<c:when test="${empty items}">
  <div class="glass empty"><p>Your cart is empty.</p><a class="btn" href="${ctx}/products">Continue shopping</a></div>
</c:when>
<c:otherwise>
<div class="cols">
  <section class="glass lines">
    <c:forEach var="it" items="${items}">
      <div class="line">
        <a class="mini" href="${ctx}/product?id=${it.product.id}">
          <c:choose><c:when test="${not empty it.product.imageUrl}"><img src="<c:out value='${it.product.imageUrl}'/>" alt=""></c:when><c:otherwise><span><c:out value="${it.product.icon}"/></span></c:otherwise></c:choose>
        </a>
        <div class="grow">
          <a class="title" href="${ctx}/product?id=${it.product.id}"><c:out value="${it.product.name}"/></a>
          <div class="muted small"><c:out value="${it.product.brand}"/></div>
          <c:if test="${it.product.stock < it.qty || !it.product.active}"><div class="stock out small">Not enough stock &mdash; adjust quantity</div></c:if>
          <div class="row sm-gap">
            <form method="post" action="${ctx}/cart" class="inline-form">
              <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="update"><input type="hidden" name="pid" value="${it.product.id}">
              <select name="qty" data-autosubmit aria-label="Quantity">
                <c:forEach begin="1" end="${it.product.maxQty > 0 ? it.product.maxQty : 1}" var="i"><option value="${i}" ${i == it.qty ? 'selected' : ''}>${i}</option></c:forEach>
              </select>
            </form>
            <form method="post" action="${ctx}/cart" class="inline-form">
              <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="remove"><input type="hidden" name="pid" value="${it.product.id}">
              <button class="link" type="submit">Remove</button>
            </form>
          </div>
        </div>
        <div class="amt"><strong><e:money v="${it.lineTotal}"/></strong><div class="muted small"><e:money v="${it.product.price}"/> each</div></div>
      </div>
    </c:forEach>
  </section>

  <aside class="glass summary">
    <h2>Order summary</h2>
    <form method="post" action="${ctx}/cart" class="coupon">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="coupon">
      <input type="text" name="code" placeholder="Coupon code" value="<c:out value='${sessionScope.coupon}'/>" maxlength="30">
      <button class="btn sm" type="submit">${empty sessionScope.coupon ? 'Apply' : 'Update'}</button>
    </form>
    <c:if test="${not empty t.couponMessage}"><p class="stock low small"><c:out value="${t.couponMessage}"/></p></c:if>
    <dl class="totals">
      <dt>Subtotal</dt><dd><e:money v="${t.subtotal}"/></dd>
      <c:if test="${t.discount.signum() > 0}"><dt>Discount (<c:out value="${t.couponCode}"/>)</dt><dd class="green">&minus; <e:money v="${t.discount}"/></dd></c:if>
      <dt>GST (18%)</dt><dd><e:money v="${t.tax}"/></dd>
      <dt>Shipping</dt><dd><c:choose><c:when test="${t.shipping.signum() == 0}">Free</c:when><c:otherwise><e:money v="${t.shipping}"/></c:otherwise></c:choose></dd>
      <dt class="grand">Total</dt><dd class="grand"><e:money v="${t.total}"/></dd>
    </dl>
    <a class="btn block" href="${ctx}/checkout">Proceed to checkout</a>
    <form method="post" action="${ctx}/cart" data-confirm="Remove everything from your cart?">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="clear">
      <button class="link center-block" type="submit">Clear cart</button>
    </form>
  </aside>
</div>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
