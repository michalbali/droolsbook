<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html><head>
<title><fmt:message key="title" /></title>
<style>
.error {
	color: red;
}
</style></head>
<body><h1><fmt:message key="customerSave.heading" /></h1>
<form:form method="post" commandName="customerSave">
	<table width="100%" bgcolor="f8f8ff" border="0" 
		cellspacing="0"	cellpadding="5">
		<c:forEach items="${errors}" var="error">
      		<span class="error"><c:out value="${error.type}"/>: 
      		<fmt:message key="${error.messageKey}"/></span><br/> 
    	</c:forEach>
    	<c:forEach items="${warnings}" var="warning">
      		<c:out value="${warning.type}"/> 
      		<fmt:message key="${warning.messageKey}"/><br/>
    	</c:forEach>
		<tr>
			<td align="right">First name:</td>
			<td><form:input path="firstName" /></td>
		</tr>
		<tr>
			<td align="right">Last name:</td>
			<td><form:input path="lastName" /></td>
		</tr>
		<tr>
			<td align="right">Phone number:</td>
			<td><form:input path="phoneNumber" /></td>
		</tr>
	</table>
	<br>
	<input type="submit" align="center" value="Execute">
</form:form>
<a href="<c:url value="index.jsp"/>">Home</a>
</body></html>