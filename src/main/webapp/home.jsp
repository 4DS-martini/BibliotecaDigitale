<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ page import="javax.servlet.http.HttpSession" %>

<%
    // Recupero la sessione esistente
    HttpSession sessione = request.getSession(false);
    
    // Controllo se l'utente è loggato
    if (sessione == null || sessione.getAttribute("user") == null) {
        response.sendRedirect("accedi.html"); // Reindirizza alla pagina di login
        return;
    }

    // Recupero l'email dell'utente dalla sessione
    String email = (String) sessione.getAttribute("user");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Home - Biblioteca Online</title>
</head>
<body>
    <h2>Benvenuto, <%= email %>!</h2>


</body>
</html>