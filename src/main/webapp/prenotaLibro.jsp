<%@ page import="java.sql.*, Database.DatabaseConnection" %>
<%
    HttpSession sessione = request.getSession(false);
    if (sessione == null || sessione.getAttribute("user") == null) {
        response.sendRedirect("accedi.html");
        return;
    }

    String email = (String) sessione.getAttribute("user");
    int libro_id = Integer.parseInt(request.getParameter("libro_id"));
    String messaggio = "";
    boolean successo = false;

    try (Connection conn = DatabaseConnection.getConnection()) {
        String utenteQuery = "SELECT id FROM utenti WHERE email = ?";
        int utente_id = -1;

        try (PreparedStatement utenteStmt = conn.prepareStatement(utenteQuery)) {
            utenteStmt.setString(1, email);
            try (ResultSet rs = utenteStmt.executeQuery()) {
                if (rs.next()) {
                    utente_id = rs.getInt("id");
                }
            }
        }

        if (utente_id != -1) {
            String checkPrenotazione = "SELECT COUNT(*) FROM prenotazioni WHERE utente_id = ? AND libro_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkPrenotazione)) {
                checkStmt.setInt(1, utente_id);
                checkStmt.setInt(2, libro_id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        messaggio = "Hai già prenotato questo libro.";
                    } else {
                        String prenotaQuery = "INSERT INTO prenotazioni (utente_id, libro_id) VALUES (?, ?)";
                        try (PreparedStatement prenotaStmt = conn.prepareStatement(prenotaQuery)) {
                            prenotaStmt.setInt(1, utente_id);
                            prenotaStmt.setInt(2, libro_id);
                            prenotaStmt.executeUpdate();
                        }

                        String updateLibri = "UPDATE libri SET copie_disponibili = copie_disponibili - 1 WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateLibri)) {
                            updateStmt.setInt(1, libro_id);
                            updateStmt.executeUpdate();
                        }

                        messaggio = "Prenotazione avvenuta con successo.";
                        successo = true;
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        messaggio = "Errore nella prenotazione.";
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Prenotazione Libro</title>
    <% if (successo) { %>
    <meta http-equiv="refresh" content="3;url=home.jsp">
    <% } %>
</head>
<body>
    <h2><%= messaggio %></h2>

    <% if (successo) { %>
        <p>Verrai reindirizzato alla home tra 3 secondi...</p>
    <% } else { %>
        <a href="home.jsp">Torna alla home</a>
    <% } %>
</body>
</html>
