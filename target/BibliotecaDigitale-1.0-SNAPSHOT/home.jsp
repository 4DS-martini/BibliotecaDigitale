<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ page import="javax.servlet.http.HttpSession, java.sql.*, Database.DatabaseConnection" %>

<%
    // Recupero la sessione esistente
    HttpSession sessione = request.getSession(false);

    if (sessione == null || sessione.getAttribute("user") == null) {
        response.sendRedirect("accedi.html"); // Reindirizza alla pagina di login
        return;
    }

    String email = (String) sessione.getAttribute("user");
    String nome = "";
    String ruolo = "";

    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT nome, ruolo FROM utenti WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nome = rs.getString("nome");
                    ruolo = rs.getString("ruolo");
                    sessione.setAttribute("ruolo", ruolo); // Salva il ruolo in sessione
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    String saluto = ruolo.equals("admin") ? "Signor" : "Gentile";
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Home - Biblioteca Online</title>
</head>
<body>
    <h2>Benvenuto, <%= saluto %> <%= nome %>!</h2>

    <% if (ruolo.equals("utente")) { %>
        <h3>Catalogo Libri</h3>
        <form action="prenotaLibro.jsp" method="post">
            <label for="libro">Scegli un libro:</label>
            <select name="libro_id">
                <%
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "SELECT id, titolo FROM libri WHERE copie_disponibili > 0";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql);
                             ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                %>
                                <option value="<%= rs.getInt("id") %>"><%= rs.getString("titolo") %></option>
                <%
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                %>
            </select>
            <button type="submit">Prenota</button>
        </form>

        <h3>I tuoi libri prenotati</h3>
        <ul>
            <%
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT libri.titolo FROM prenotazioni " +
                                 "JOIN libri ON prenotazioni.libro_id = libri.id " +
                                 "JOIN utenti ON prenotazioni.utente_id = utenti.id " +
                                 "WHERE utenti.email = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, email);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
            %>
                                <li><%= rs.getString("titolo") %></li>
            <%
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            %>
        </ul>
    <% } else if (ruolo.equals("admin")) { %>
        <h3>Gestione Libri</h3>
        <form action="gestioneLibri.jsp" method="post">
            <button type="submit" name="azione" value="aggiungi">Aggiungi Libro</button>
            <button type="submit" name="azione" value="modifica">Modifica Libro</button>
            <button type="submit" name="azione" value="elimina">Elimina Libro</button>
        </form>
    <% } %>

</body>
</html>
