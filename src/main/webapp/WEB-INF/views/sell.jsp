<c:set var="title" value="Sell on ecart"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<section class="hero glass">
  <div>
    <span class="eyebrow">For shops &amp; distributors</span>
    <h1>Sell on ecart</h1>
    <p>Reach customers online. List your products, ship orders and track your GST &mdash; all from one seller dashboard.</p>
    <div class="row"><a class="btn" href="${ctx}/sell/register">Register your shop</a><a class="btn ghost" href="${ctx}/login?portal=seller">Seller / Admin login</a></div>
  </div>
  <div class="hero-art" aria-hidden="true"><span>&#127970;</span></div>
</section>

<div class="cols even">
  <section class="glass form-card">
    <h2>Keep these ready</h2>
    <div class="sum-line"><span>PAN of the business or owner</span><strong>Required</strong></div>
    <div class="sum-line"><span>GSTIN (15 characters)</span><strong>Required*</strong></div>
    <div class="sum-line"><span>Pickup / shop address with pincode</span><strong>Required</strong></div>
    <div class="sum-line"><span>Bank account number and IFSC</span><strong>Required</strong></div>
    <div class="sum-line"><span>Owner mobile number and email</span><strong>Required</strong></div>
    <p class="muted small">*Small sellers without GST registration can tick &ldquo;I do not have a GSTIN&rdquo;. They cannot charge GST, so their products are billed at 0%.</p>
  </section>
  <section class="glass form-card">
    <h2>How it works</h2>
    <div class="sum-line"><span>1. Register your shop</span><strong>5 minutes</strong></div>
    <div class="sum-line"><span>2. ecart verifies GSTIN &amp; PAN</span><strong>Review</strong></div>
    <div class="sum-line"><span>3. Add products with GST rate &amp; HSN</span><strong>Your catalogue</strong></div>
    <div class="sum-line"><span>4. Confirm, ship (courier + tracking) and deliver orders</span><strong>Your orders</strong></div>
    <div class="sum-line"><span>5. See sales and GST collected</span><strong>Dashboard</strong></div>
    <p class="muted small">Demo store: payouts and payments are simulated, no real money moves.</p>
  </section>
</div>
<div class="row"><a class="btn" href="${ctx}/sell/register">Register your shop</a><a class="btn ghost" href="${ctx}/login?portal=seller">Already a seller? Sign in</a></div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
