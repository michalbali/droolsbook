<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
  <head><title><fmt:message key="title"/></title></head>
  <body>
    <h1><fmt:message key="heading"/></h1>
    <p><fmt:message key="greeting"/> <c:out value="${model.now}"/></p>
    <h3>Customers</h3>
    <c:forEach items="${model.customers}" var="customer">
      <c:out value="${customer.firstName}"/> <c:out value="${customer.lastName}"/><br><br>
    </c:forEach>
    <br>
    <!-- the following two lines doesn't make sence on teh same page, this is secuirty driven, this page is just a mock -->
    <a href="<c:url value="customerSave.htm"/>">Add Customer</a><br />
    <a href="<c:url value="loanRequest.htm"/>">Loan Request</a><br />
    <a href="<c:url value="taskList.htm"/>">Task List</a><br /><br />
    
    <a href="<c:url value="approveEvent.htm"/>">Approve Event</a><br />
    <a href="<c:url value="listPendingTasks.htm"/>">List Tasks Waiting For Approve Event</a> (logger)<br />
    <br>    
  </body>
</html>