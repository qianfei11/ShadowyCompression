<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="Shadowy-Compression.jpg" type="image/x-icon" />
<title>Shadowy Compression</title>
</head>

<body>
	<center>
		<h1>Shadowy Compression</h1>

		<div>
			<img src="Shadowy-Compression.jpg" width="256" height="256"
				border="4">
		</div>
		<br>

		<form method="post" action="UploadServlet"
			enctype="multipart/form-data">
			Select a bmp file: <input type="file" name="uploadFile"> <br>
			<br> <input type="submit" value="Upload">
		</form>
		<br>
	</center>
</body>
</html>