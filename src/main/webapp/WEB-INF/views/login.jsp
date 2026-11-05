<c:set var="title" value="Sign in"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<section class="glass auth">
  <h1>${portal == 'seller' ? 'Seller / Admin login' : 'Welcome back'}</h1>
  <p class="muted">${portal == 'seller' ? 'Sign in to manage your shop, products and orders.' : 'Sign in to your ecart account.'}</p>
  <c:if test="${not empty error}"><div class="flash err"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/login">
    <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="next" value="<c:out value='${next}'/>"><input type="hidden" name="portal" value="<c:out value='${portal}'/>">
    <label>Email<input type="email" name="email" value="<c:out value='${email}'/>" required autofocus autocomplete="username"></label>
    <label>Password<input type="password" name="password" required autocomplete="current-password"></label>
    <button class="btn block" type="submit">Sign in</button>
  </form>
  <p class="muted center">New here? <a href="${ctx}/register">Create an account</a> &middot; Want to sell? <a href="${ctx}/sell/register">Register your shop</a></p>
</section>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
