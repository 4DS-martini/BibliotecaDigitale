package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Database.DatabaseConnection;
import org.json.JSONObject;

@WebServlet("/PrenotaLibroServlet")
public class PrenotaLibroServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sessione = request.getSession(false);
        JSONObject jsonResponse = new JSONObject();

        // Verifica se l'utente è loggato
        if (sessione == null || sessione.getAttribute("user") == null) {
            jsonResponse.put("error", "Non sei loggato. Effettua il login.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendResponse(response, jsonResponse);
            return;
        }

        String email = (String) sessione.getAttribute("user");
        int libro_id;
        try {
            libro_id = Integer.parseInt(request.getParameter("libro_id"));
        } catch (NumberFormatException e) {
            jsonResponse.put("error", "ID del libro non valido.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendResponse(response, jsonResponse);
            return;
        }

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

        // Costruzione della risposta JSON
        jsonResponse.put("messaggio", messaggio);
        jsonResponse.put("successo", successo);

        if (successo) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        sendResponse(response, jsonResponse);
    }

    // Metodo per inviare la risposta JSON
    private void sendResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
}