<%@page import="org.dew.webfolder.*"%>
<%
	User user = WebLogin.getUserLogged(request);
	
	Object message  = request.getAttribute("message");
	Object username = request.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="it">
	<head>
		<title>WebFolder</title>
		<link href="css/style.css?<%= WebFile.STARTUP_TIME %>" rel="stylesheet">
	</head>
	<body>
		<h1 class="title">WebFolder</h1>
		<% if(user != null) {%>
			<div id="header-actions"><a href="logout" title="Logout">Logout</a></div>
		<% } %>
		<hr>
		<div id="main-container">
		<% if(user == null) { %>
			<form class="form-signin" method="POST" action="login">
				<label for="j_username">Username</label>
				<input type="text" id="j_username" name="j_username" placeholder="Username" value="<%= username != null ? username : "" %>" required autofocus>
				<br />
				<label for="j_password">Password</label>
				<input type="password" id="j_password" name="j_password" placeholder="Password" required>
				<br />
				<br />
				<button type="submit">Login</button>
			</form>
		<% } else { %>
			<%= new ViewFolder(user) %>
		<% } %>
		</div>
		<hr>
		<% if(user != null) {%>
			<div id="footer-actions"><a href="logout" title="Logout">Logout</a></div>
		<% } %>
		<h3 class="message"><%= message != null ? message : "" %></h3>
		<script src="js/tree.js?<%= WebFile.STARTUP_TIME %>"></script>
	</body>
</html>