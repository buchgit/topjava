<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://example.com/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<style>
    <%@include file="/WEB-INF/css/style.css" %>
</style>
<html lang="ru">
<head>
    <title>Meal</title>
</head>
<body>

<h3><a href="index.html">Home</a></h3>
<hr>
<h2 style="text-align: center; margin-top: 50px; margin-bottom: 30px">Meals</h2>
<table>
    <thead>
    <tr>
        <th scope="col">Id</th>
        <th scope="col">Date/Time</th>
        <th scope="col">Description</th>
        <th scope="col">Calories</th>
        <th scope="col">up</th>
        <th scope="col">del</th>
    </tr>
    </thead>
    <tbody>
    <jsp:useBean id="mealsList" scope="request" type="java.util.List"/>
    <c:forEach items="${mealsList}" var="meal" varStatus="counter">
        <c:set var="excess" value="${meal.excess}"/>
        <c:set var="mealId" value="${meal.id}"/>
        <tr style="color:${excess eq true ? 'red' : 'green'}">
            <td>${mealId}</td>
            <td>${f:formatLocalDateTime(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="?action=insert&id=${mealId}">update</a></td>
            <td><a href="?action=delete&id=<c:out value="${mealId}"/>">delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<p><a href="?action=insert">Add meal</a></p>
</body>
</html>