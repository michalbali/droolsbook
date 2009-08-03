<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><fmt:message key="title" /></title>
<style>
.error {
	color: red;
}
</style>
</head>
<body>
<h1><fmt:message key="loanRequest.heading" /></h1>
<form:form method="post" commandName="loanRequest">
	<table width="100%" bgcolor="f8f8ff" border="0" 
		cellspacing="0"	cellpadding="5">
		<tr>
			<td align="right">Amount:</td>
			<td><form:input path="amount" /></td>			
		</tr>
		<tr>
			<td align="right">Duration:</td>
			<td><form:input path="durationYears" /> years</td>			
		</tr>
	</table>
	<br>
	<input type="submit" align="center" value="Execute">
</form:form>
<a href="<c:url value="customerList.htm"/>">Home</a>
</body>
</html>