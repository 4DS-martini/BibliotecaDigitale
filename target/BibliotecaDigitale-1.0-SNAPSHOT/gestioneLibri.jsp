<%@ page import="java.sql.*, javax.servlet.http.HttpSession, Database.DatabaseConnection" %>

<%
    // Recupero la sessione
    HttpSession sessione = request.getSession(false);

    if (sessione == null || sessione.getAttribute("user") == null) {
        response.sendRedirect("accedi.html");
        return;
    }

    String ruolo = (String) sessione.getAttribute("ruolo");

    // Controllo se l'utente è admin
    if (!"admin".equals(ruolo)) {
        response.sendRedirect("home.jsp"); // Se non è admin, torna alla home
        return;
    }

    String azione = request.getParameter("azione");

    if ("aggiungi".equals(azione)) {
%>
        <h2>Aggiungi un Libro</h2>
        <form action="aggiungiLibro.jsp" method="post">
            <label>Titolo: <input type="text" name="titolo" required></label><br>
            <label>Autore: <input type="text" name="autore" required></label><br>
            <label>Anno Pubblicazione: <input type="number" name="anno_pubblicazione" required></label><br>
            <label>Genere: <input type="text" name="genere" required></label><br>
            <label>Copie Disponibili: <input type="number" name="copie_disponibili" required></label><br>
            <button type="submit">Aggiungi Libro</button>
        </form>
<%
    } else if ("modifica".equals(azione)) {
%>
        <h2>Modifica un Libro</h2>
        <form action="modificaLibro.jsp" method="post">
            <label>ID Libro: <input type="number" name="id" required></label><br>
            <label>Nuovo Titolo: <input type="text" name="titolo"></label><br>
            <label>Nuovo Autore: <input type="text" name="autore"></label><br>
            <label>Nuovo Anno: <input type="number" name="anno_pubblicazione"></label><br>
            <label>Nuovo Genere: <input type="text" name="genere"></label><br>
            <label>Nuove Copie: <input type="number" name="copie_disponibili"></label><br>
            <button type="submit">Modifica Libro</button>
        </form>
<%
    } else if ("elimina".equals(azione)) {
%>
        <h2>Elimina un Libro</h2>
        <form action="eliminaLibro.jsp" method="post">
            <label>ID Libro: <input type="number" name="id" required></label><br>
            <button type="submit">Elimina Libro</button>
        </form>
<%
    }
%>
