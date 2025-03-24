<%-- 
    Document   : home
    Created on : 24 mar 2025, 09:10:46
    Author     : loris.martini
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - Benvenuto</title>
    <link rel="stylesheet" href="css/style.css"> <!-- Aggiungi il tuo file di stile -->
</head>
<body>
    <header>
        <nav>
            <ul>
                <li><a href="home.jsp">Home</a></li>
                <li><a href="profilo.jsp">Il mio profilo</a></li>
                <li><a href="prenotazioni.jsp">Le mie prenotazioni</a></li>
                <li><a href="logout.jsp">Logout</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <div class="container">
            <h1>Benvenuto, </h1>
            <p>Ruolo: </p>
            
            <h2>Panoramica:</h2>
            <p>Benvenuto nella tua home page. Qui puoi gestire le tue prenotazioni, visualizzare il tuo profilo, e tanto altro!</p>

            <h3>Azioni rapide:</h3>
            <ul>
                <li><a href="prenotazioni.jsp">Visualizza le tue prenotazioni</a></li>
                <li><a href="profilo.jsp">Modifica il tuo profilo</a></li>
            </ul>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 Biblioteca Online - Tutti i diritti riservati.</p>
    </footer>
</body>
</html>