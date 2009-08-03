<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
  <head><title><fmt:message key="title"/></title></head>
  <body>
    <h1><fmt:message key="taskList.heading"/></h1>
<table width="100%" bgcolor="f8f8ff" border="0" 
	cellspacing="0"	cellpadding="5">
  <tr>
    <td>id</td><td>priority</td><td>status</td><td>name</td>
  </tr>
  <c:forEach items="${model.tasks}" var="task">
  <tr>
    <td><c:out value="${task.id}"/></td>
    <td><c:out value="${task.priority}"/></td>
  	<td><c:out value="${task.status}"/> </td>
    <td><c:out value="${task.name}"/></td>
    <td>       
      <a href="<c:url value="taskClaim.htm"><c:param 
       name="taskId" value="${task.id}"/></c:url>">Claim</a>
      <a href="<c:url value="taskStart.htm"><c:param 
       name="taskId" value="${task.id}"/></c:url>">Start</a>
      <a href="<c:url value="taskComplete.htm"><c:param 
       name="taskId" value="${task.id}"/></c:url>">Complete</a>
  	</td>
  </tr>
  </c:forEach>
</table>
    <br> 
    <a href="<c:url value="customerList.htm"/>">Home</a>   
  </body>
</html>