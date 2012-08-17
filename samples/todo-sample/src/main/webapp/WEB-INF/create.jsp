<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Create</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container">
<h1>Create a new todo:</h1>

<form class="form-horizontal" method="post" action="new">
        <fieldset>
          <div class="control-group <c:if test="${titleError}" >error</c:if>">
            <label class="control-label" for="title">Title</label>
            <div class="controls">
              <input type="text" class="input-xlarge" id="title" name="title" value="<c:out value="${title}" />">
              <c:if test="${titleError}" ><span class="help-inline">The field is mandatory</span></c:if>
            </div>
          </div>
          <div class="control-group <c:if test="${descriptionError}" >error</c:if>">
            <label class="control-label" for="description">Description</label>
            <div class="controls">
              <textarea class="input-xlarge" id="description" name="description" rows="3"><c:out value="${description}" /></textarea>
              <c:if test="${descriptionError}" ><span class="help-inline">The field is mandatory</span></c:if>
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
