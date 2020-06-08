<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Add new meal</title>
</head>
<body>
<%--<p>Name: <%= request.getParameter("name") %></p>--%>
<form method="POST" action='' name="frmAddMeal">
    <jsp:useBean id="mealId" class="java.lang.String" scope="request"  />
    <jsp:useBean id="description" class="java.lang.String" scope="request"  />
    <jsp:useBean id="dateTime" class="java.lang.String" scope="request"  />
    <jsp:useBean id="calories" class="java.lang.String" scope="request"  />
    Meal ID : <input type="text" name="id" readonly="readonly"
                     value="<c:out value="${mealId}" />" /> <br />
    Description : <input
        type="text" name="description"
        value="<c:out value="${description}" />" /> <br />
    Date/Time : <input
        type="datetime-local" name="dateTime"
        value="<c:out value="${dateTime}" />" /> <br />
    Calories : <input type="text" name="calories"
                   value="<c:out value="${calories}" />" /> <br /> <input
        type="submit" value="Submit" />
</form>
</body>
</html>
