<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Index</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container">
<h1>TODO list</h1>
<table class="table table-bordered table-striped">
  <thead>
    <tr>
      <th>Title</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
<c:forEach items="${todos}" var="todo">
<tr>
  <td><c:out value="${todo.title}" /></td>
  <td><c:out value="${todo.description}" /></td>
</tr>
</c:forEach>
  </tbody>
</table>

<hr/>
<a class="btn btn-primary" href="new">Create</a>
</div>

</body>
</html>
