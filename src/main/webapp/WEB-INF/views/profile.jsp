<c:set var="title" value="Profile"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<h1 class="page-title">Profile &amp; security</h1>
<div class="cols even">
  <section class="glass form-card">
    <h2>Your details</h2>
    <form method="post" action="${ctx}/profile">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="info">
      <label>Full name<input type="text" name="name" value="<c:out value='${sessionScope.user.name}'/>" required maxlength="100"></label>
      <label>Email<input type="email" value="<c:out value='${sessionScope.user.email}'/>" disabled></label>
      <label>Mobile<input type="tel" name="phone" value="<c:out value='${sessionScope.user.phone}'/>" pattern="[6-9][0-9]{9}" maxlength="10"></label>
      <button class="btn" type="submit">Save changes</button>
    </form>
  </section>
  <section class="glass form-card">
    <h2>Change password</h2>
    <form method="post" action="${ctx}/profile">
      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="action" value="password">
      <label>Current password<input type="password" name="current" required autocomplete="current-password"></label>
      <label>New password<input type="password" name="password" required minlength="8" autocomplete="new-password"></label>
      <label>Confirm new password<input type="password" name="confirm" required minlength="8" autocomplete="new-password"></label>
      <button class="btn" type="submit">Update password</button>
    </form>
  </section>
</div>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
