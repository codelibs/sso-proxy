<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@page import="jp.sf.ssoproxy.SSOProxyConstraints"%>
<fmt:setBundle basename="jp.sf.ssoproxy.resource.Messages" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title><fmt:message key="error.title" /></title>
<style><!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style>
</head>
<body>
<h1><fmt:message key="error.errorCode" /> <%= request.getAttribute(SSOProxyConstraints.ERROR_CODE) %></h1>
<hr size="1" noshade="noshade">
<p><b><fmt:message key="error.message" /></b> <u><%= request.getAttribute(SSOProxyConstraints.ERROR_MESSAGE) %></u></p>
<hr size="1" noshade="noshade">
<h3><fmt:message key="error.proxyName" /></h3>
</body>
</html>
