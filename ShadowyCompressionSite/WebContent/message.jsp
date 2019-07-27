<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Result</title>
</head>
<body>
	<center>
		<h2>${message}</h2>

		<h3>Huffman Encode takes ${huffmanTime} ms</h3>
		<h3>Total time is ${totalTime} ms</h3>
		<h3>Client takes ${clientTime} ms</h3>

		<h2>Here is the result:</h2>

		<div style="text-align: center">
			<img src="upload/${source}" width="256" height="256"> <img
				src="Arrow.jpg" width="75" height="256"> <img
				src="upload/${result}" width="256" height="256">
		</div>
		<div style="columns: 4">
			<div>&nbsp;</div>
			<div style="text-align: center">Original: ${srcSize}B</div>
			<div style="text-align: center">Compressed: ${dstSize}B</div>
			<div>&nbsp;</div>
		</div>

	</center>
</body>
</html>
