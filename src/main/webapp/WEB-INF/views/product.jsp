<c:set var="title" value="${p.name}"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<nav class="crumbs"><a href="${ctx}/">Home</a> / <a href="${ctx}/products?cat=${p.categoryId}"><c:out value="${p.categoryName}"/></a> / <c:out value="${p.name}"/></nav>
<section class="detail">
  <div class="glass gallery">
    <c:choose>
      <c:when test="${not empty p.imageUrl}"><img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>"></c:when>
      <c:otherwise><span class="ph big"><c:out value="${p.icon}"/></span></c:otherwise>
    </c:choose>
  </div>
  <div class="glass info">
    <div class="brand"><c:out value="${p.brand}"/></div>
    <h1><c:out value="${p.name}"/></h1>
    <p class="muted small">Sold by <strong><c:out value="${p.soldBy}"/></strong></p>
    <div class="rate">
      <c:choose>
        <c:when test="${p.reviewCount > 0}"><e:stars n="${p.roundedRating}"/> <a href="#reviews"><fmt:formatNumber value="${p.avgRating}" maxFractionDigits="1"/> &middot; ${p.reviewCount} review<c:if test="${p.reviewCount != 1}">s</c:if></a></c:when>
        <c:otherwise><a class="muted" href="#reviews">No reviews yet</a></c:otherwise>
      </c:choose>
    </div>
    <div class="price lg"><strong><e:money v="${p.price}"/></strong>
      <c:if test="${p.discountPercent > 0}"><s><e:money v="${p.mrp}"/></s> <span class="badge off inline">${p.discountPercent}% off</span></c:if>
    </div>
    <p class="muted small">Inclusive of offers. GST (<fmt:formatNumber value="${p.gstPercent}" maxFractionDigits="1"/>%) is added at checkout.</p>
    <p class="desc"><c:out value="${p.description}"/></p>
    <c:choose>
      <c:when test="${p.stock == 0}"><p class="stock out">Currently out of stock</p></c:when>
      <c:otherwise>
        <p class="stock ${p.stock <= 5 ? 'low' : 'in'}"><c:out value="${p.stock <= 5 ? 'Only '.concat(p.stock).concat(' left!') : 'In stock'}"/></p>
        <form method="post" action="${ctx}/cart" class="buy">
          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="add"><input type="hidden" name="pid" value="${p.id}">
          <label class="qty">Qty
            <select name="qty"><c:forEach begin="1" end="${p.maxQty}" var="i"><option value="${i}">${i}</option></c:forEach></select>
          </label>
          <button class="btn" type="submit">Add to cart</button>
          <button class="btn dark" type="submit" name="buy" value="1">Buy now</button>
        </form>
      </c:otherwise>
    </c:choose>
    <form method="post" action="${ctx}/wishlist" class="inline-form">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="pid" value="${p.id}">
      <button class="btn ghost sm" type="submit">${wished.contains(p.id) ? '&#9829; In wishlist' : '&#9825; Add to wishlist'}</button>
    </form>
  </div>
</section>

<section id="reviews" class="glass reviews">
  <h2>Customer reviews</h2>
  <c:if test="${not empty sessionScope.user}">
    <form method="post" action="${ctx}/review" class="review-form">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="pid" value="${p.id}">
      <div class="two">
        <label>Rating
          <select name="rating" required><option value="">Choose&hellip;</option>
            <option value="5">5 - Excellent</option><option value="4">4 - Good</option><option value="3">3 - Okay</option><option value="2">2 - Poor</option><option value="1">1 - Bad</option>
          </select>
        </label>
        <label>Headline<input type="text" name="title" maxlength="120" placeholder="Sum it up"></label>
      </div>
      <label>Your review<textarea name="body" rows="3" maxlength="1500" placeholder="What did you like or dislike?"></textarea></label>
      <button class="btn sm" type="submit">Submit review</button>
    </form>
  </c:if>
  <c:if test="${empty sessionScope.user}"><p class="muted"><a href="${ctx}/login?next=/product%3Fid%3D${p.id}">Sign in</a> to write a review.</p></c:if>
  <c:forEach var="r" items="${reviews}">
    <div class="review">
      <div><e:stars n="${r.rating}"/> <strong><c:out value="${r.title}"/></strong></div>
      <div class="muted small"><c:out value="${r.userName}"/> &middot; <fmt:formatDate value="${r.createdAt}" pattern="d MMM yyyy"/>
        <c:if test="${r.verified}"> &middot; <span class="ver">Verified purchase</span></c:if></div>
      <p><c:out value="${r.body}"/></p>
    </div>
  </c:forEach>
</section>

<c:if test="${not empty related}">
<section><div class="sec-head"><h2>You may also like</h2></div>
  <div class="grid"><c:forEach var="x" items="${related}"><e:card p="${x}" wished="${wished.contains(x.id)}"/></c:forEach></div>
</section>
</c:if>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
