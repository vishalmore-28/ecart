<c:set var="title" value="Create account"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<section class="glass auth">
  <h1>Create your account</h1>
  <p class="muted">Takes less than a minute.</p>
  <c:if test="${not empty error}"><div class="flash err"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/register">
    <input type="hidden" name="_csrf" value="${csrf}">
    <label>Full name<input type="text" name="name" value="<c:out value='${name}'/>" required maxlength="100" autocomplete="name"></label>
    <label>Email<input type="email" name="email" value="<c:out value='${email}'/>" required autocomplete="email"></label>
    <label>Mobile (optional)<input type="tel" name="phone" value="<c:out value='${phone}'/>" pattern="[6-9][0-9]{9}" maxlength="10" placeholder="10-digit number" autocomplete="tel"></label>
    <label>Password<input type="password" name="password" required minlength="8" autocomplete="new-password"></label>
    <label>Confirm password<input type="password" name="confirm" required minlength="8" autocomplete="new-password"></label>
    <p class="muted small">At least 8 characters, with letters and numbers.</p>
    <button class="btn block" type="submit">Create account</button>
  </form>
  <p class="muted center">Already registered? <a href="${ctx}/login">Sign in</a></p>
</section>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
