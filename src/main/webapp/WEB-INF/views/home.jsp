<c:set var="title" value="Home"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<section class="hero glass">
  <div>
    <span class="eyebrow">New season &middot; new tech</span>
    <h1>Electronics, made simple.</h1>
    <p>Phones, laptops, audio and more &mdash; honest prices, fast delivery and easy returns.</p>
    <div class="row"><a class="btn" href="${ctx}/products">Shop all products</a><a class="btn ghost" href="${ctx}/products?sort=discount">Today's deals</a></div>
    <p class="muted small">Use code <strong>WELCOME10</strong> for 10% off (min. &#8377;1,000).</p>
  </div>
  <div class="hero-art" aria-hidden="true"><span>&#128241;</span><span>&#128187;</span><span>&#127911;</span></div>
</section>

<section>
  <div class="cat-grid">
    <c:forEach var="k" items="${navCategories}">
      <a class="glass cat" href="${ctx}/products?cat=${k.id}"><span><c:out value="${k.icon}"/></span><c:out value="${k.name}"/></a>
    </c:forEach>
  </div>
</section>

<section>
  <div class="sec-head"><h2>Top deals</h2><a href="${ctx}/products?sort=discount">See all &rarr;</a></div>
  <div class="grid"><c:forEach var="p" items="${deals}"><e:card p="${p}" wished="${wished.contains(p.id)}"/></c:forEach></div>
</section>

<section>
  <div class="sec-head"><h2>New arrivals</h2><a href="${ctx}/products">See all &rarr;</a></div>
  <div class="grid"><c:forEach var="p" items="${latest}"><e:card p="${p}" wished="${wished.contains(p.id)}"/></c:forEach></div>
</section>

<section class="glass hero sellhero">
  <div>
    <span class="eyebrow">For shops &amp; distributors</span>
    <h2>Sell on ecart</h2>
    <p>Register your shop with GST details, list products and manage orders from your own seller dashboard.</p>
    <div class="row"><a class="btn" href="${ctx}/sell/register">Register your shop</a><a class="btn ghost" href="${ctx}/login?portal=seller">Seller / Admin login</a><a class="btn ghost" href="${ctx}/sell">What you need</a></div>
  </div>
  <div class="hero-art" aria-hidden="true"><span>&#127970;</span></div>
</section>

<section class="trust">
  <div class="glass"><strong>Free shipping</strong><span>On orders above &#8377;5,000</span></div>
  <div class="glass"><strong>Secure checkout</strong><span>Protected sessions &amp; hashed passwords</span></div>
  <div class="glass"><strong>Easy cancellation</strong><span>Cancel before it ships, refund on paid orders</span></div>
  <div class="glass"><strong>Verified reviews</strong><span>Real feedback from real buyers</span></div>
</section>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
