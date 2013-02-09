<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
  <head><title><fmt:message key="title"/></title></head>
  <body>
    <h1><fmt:message key="heading"/></h1>
    <p><fmt:message key="greeting"/> <c:out value="${now}"/></p>
    <h3>Customers</h3>
    <c:forEach items="${customers}" var="customer">
      <c:out value="${customer.firstName}"/> <c:out value="${customer.lastName}"/><br><br>
    </c:forEach>
    <br>
    <!-- the following two lines doesn't make sense on the same page, this is security driven, this page is just a mock -->
    <a href="<c:url value="customerSave.htm"/>">Add Customer</a><br />
    <a href="<c:url value="loanRequest.htm"/>">Loan Request</a><br />
    <a href="<c:url value="taskList.htm"/>">Task List</a><br /><br />
    
    <a href="<c:url value="approveEvent.htm"/>">Approve Event</a><br />
    <a href="<c:url value="listPendingTasks.htm"/>">List Tasks Waiting For Approve Event</a> (logger)<br />
    <br>    
  </body>
</html>