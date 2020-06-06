<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@taglib uri="http://example.com/functions" prefix="f" %>

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
        <th scope="col">#</th>
        <th scope="col">Description</th>
        <th scope="col">Date/Time</th>
        <th scope="col">Calories</th>
        <th scope="col">Excess</th>
    </tr>
    </thead>
    <tbody>
    <jsp:useBean id="mealsList" scope="request" type="java.util.List"/>
    <c:forEach items="${mealsList}" var="meal" varStatus="counter">
        <c:set var="excess" value="${meal.excess}"/>

        <c:choose>
            <c:when test="${excess eq false}">
                <tr style="color: red">
                    <td><c:out value="${counter.count}"/></td>
                    <td><c:out value="${meal.description}"/></td>
                    <td><c:out value="${f:formatLocalDateTime(meal.dateTime, 'dd.MM.yyyy hh:mm:ss')}"/></td>
                    <td><c:out value="${meal.calories}"/></td>
                    <td><c:out value="${excess}"/></td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr style="color: #4CAF50">
                    <td><c:out value="${counter.count}"/></td>
                    <td><c:out value="${meal.description}"/></td>
                    <td><c:out value="${f:formatLocalDateTime(meal.dateTime, 'dd.MM.yyyy hh:mm:ss')}"/></td>
                    <td><c:out value="${meal.calories}"/></td>
                    <td><c:out value="${excess}"/></td>
                </tr>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    </tbody>
</table>

<button style="margin-bottom: 15px" type="button" class="btn btn-info" onclick="addNote()">Add note</button>

</body>
</html>