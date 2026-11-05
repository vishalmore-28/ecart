<c:set var="title" value="Register your shop"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<section class="glass form-card wide">
  <h1 class="page-title">Register your shop</h1>
  <p class="muted">Your details are checked by the ecart team before your shop goes live. GSTIN, PAN and state must agree with each other.</p>
  <c:if test="${not empty error}"><div class="flash err"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/sell/register">
    <input type="hidden" name="_csrf" value="${csrf}">

    <h2>1. Owner &amp; login</h2>
    <div class="two">
      <label>Owner full name<input type="text" name="ownerName" value="<c:out value='${f.ownerName}'/>" required maxlength="100" autocomplete="name"></label>
      <label>Mobile<input type="tel" name="phone" value="<c:out value='${f.phone}'/>" required pattern="[6-9][0-9]{9}" maxlength="10" placeholder="10-digit number" autocomplete="tel"></label>
    </div>
    <label>Email (your login)<input type="email" name="email" value="<c:out value='${f.email}'/>" required autocomplete="email"></label>
    <div class="two">
      <label>Password<input type="password" name="password" required minlength="8" autocomplete="new-password"></label>
      <label>Confirm password<input type="password" name="confirm" required minlength="8" autocomplete="new-password"></label>
    </div>

    <h2>2. Business &amp; GST</h2>
    <div class="two">
      <label>Shop / business name<input type="text" name="shopName" value="<c:out value='${f.shopName}'/>" required maxlength="120"></label>
      <label>Business type
        <select name="businessType"><c:forEach var="t" items="${types}"><option value="${t}" ${f.businessType == t ? 'selected' : ''}>${t}</option></c:forEach></select>
      </label>
    </div>
    <div class="two">
      <label>PAN<input type="text" name="pan" value="<c:out value='${f.pan}'/>" required maxlength="10" pattern="[A-Za-z]{5}[0-9]{4}[A-Za-z]" placeholder="ABCDE1234F"></label>
      <label>GSTIN<input type="text" name="gstin" value="<c:out value='${f.gstin}'/>" maxlength="15" placeholder="15 characters, e.g. 27AAPFU0939F1ZV"></label>
    </div>
    <label class="check"><input type="checkbox" name="noGst" ${f.noGst == '1' ? 'checked' : ''}> I do not have a GSTIN (unregistered seller &mdash; no GST can be charged)</label>

    <h2>3. Pickup address</h2>
    <label>Address line 1<input type="text" name="addressLine1" value="<c:out value='${f.addressLine1}'/>" required maxlength="200"></label>
    <label>Address line 2 (optional)<input type="text" name="addressLine2" value="<c:out value='${f.addressLine2}'/>" maxlength="200"></label>
    <div class="three">
      <label>City<input type="text" name="city" value="<c:out value='${f.city}'/>" required maxlength="80"></label>
      <label>State
        <select name="state" required><option value="">Choose state</option><c:forEach var="st" items="${states}"><option value="${st}" ${f.state == st ? 'selected' : ''}>${st}</option></c:forEach></select>
      </label>
      <label>Pincode<input type="text" name="pincode" value="<c:out value='${f.pincode}'/>" required pattern="[0-9]{6}" maxlength="6"></label>
    </div>

    <h2>4. Bank account for payouts</h2>
    <div class="two">
      <label>Account holder name<input type="text" name="bankHolder" value="<c:out value='${f.bankHolder}'/>" required maxlength="100"></label>
      <label>IFSC<input type="text" name="ifsc" value="<c:out value='${f.ifsc}'/>" required maxlength="11" pattern="[A-Za-z]{4}0[A-Za-z0-9]{6}" placeholder="HDFC0001234"></label>
    </div>
    <div class="two">
      <label>Account number<input type="password" name="account" required inputmode="numeric" pattern="[0-9 \-]{9,24}" autocomplete="off"></label>
      <label>Confirm account number<input type="text" name="account2" required inputmode="numeric" pattern="[0-9 \-]{9,24}" autocomplete="off"></label>
    </div>
    <p class="muted small">Demo store: only the last 4 digits of the account number are saved.</p>

    <label class="check"><input type="checkbox" name="terms"> I confirm the details are correct and I accept the seller terms.</label>
    <div class="row"><button class="btn" type="submit">Submit application</button><a class="btn ghost" href="${ctx}/sell">Cancel</a></div>
  </form>
</section>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
