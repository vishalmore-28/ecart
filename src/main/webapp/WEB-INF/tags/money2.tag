<%@ tag body-content="empty" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="v" required="true" type="java.lang.Object" %>&#8377;<fmt:formatNumber value="${v}" pattern="#,##0.00"/>
