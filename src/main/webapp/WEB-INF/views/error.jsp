<%@ page isErrorPage="true" %>
<c:set var="code" value="${pageContext.errorData.statusCode}"/>
<c:set var="title" value="Error ${code}"/>
<%@ include file="/WEB-INF/views/common/top.jspf" %>
<section class="glass center-card">
  <h1>${code}</h1>
  <c:choose>
    <c:when test="${code == 404}"><p>We couldn't find that page.</p></c:when>
    <c:when test="${code == 403}"><p>You don't have permission to do that (or your session expired). Please go back, refresh and try again.</p></c:when>
    <c:otherwise><p>Something went wrong on our side. Please try again in a moment.</p></c:otherwise>
  </c:choose>
  <a class="btn" href="${ctx}/">Back to home</a>
</section>
<%@ include file="/WEB-INF/views/common/bottom.jspf" %>
