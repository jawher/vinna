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
<h2>TODO list:</h2>
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
  <td><a href="<c:out value="${todo.id}" />"><c:out value="${todo.title}" /></a></td>
  <td><c:out value="${todo.description}" /></td>
</tr>
</c:forEach>
  </tbody>
</table>

<hr/>
<h2>Create a todo</h2>
<form class="form-horizontal" method="post" action="new">
        <fieldset>
          <div class="control-group">
            <label class="control-label" for="title">Title</label>
            <div class="controls">
              <input type="text" class="input-xlarge" id="title" name="title">
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="description">Description</label>
            <div class="controls">
              <textarea class="input-xlarge" id="description" name="description" rows="3"></textarea>
            </div>
          </div>
          <div class="control-group">
            <div class="controls">
            <button type="submit" class="btn btn-primary">Create</button>
            </div>
          </div>
        </fieldset>
      </form>
</div>

</body>
</html>
