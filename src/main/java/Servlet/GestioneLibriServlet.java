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
import java.sql.SQLException;
import Database.DatabaseConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

@WebServlet("/GestioneLibriServlet")
public class GestioneLibriServlet extends HttpServlet {

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

        String ruolo = (String) sessione.getAttribute("ruolo");

        // Verifica se l'utente è admin
        if (!"admin".equals(ruolo)) {
            jsonResponse.put("error", "Accesso negato. Solo gli amministratori possono gestire i libri.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            sendResponse(response, jsonResponse);
            return;
        }

        String azione = request.getParameter("azione");

        switch (azione) {
            case "aggiungi":
                aggiungiLibro(request, jsonResponse, response);
                break;
            case "modifica":
                modificaLibro(request, jsonResponse, response);
                break;
            case "elimina":
                eliminaLibro(request, jsonResponse, response);
                break;
            default:
                jsonResponse.put("error", "Azione non valida.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendResponse(response, jsonResponse);
                break;
        }
    }

    // Metodo per aggiungere un libro
    private void aggiungiLibro(HttpServletRequest request, JSONObject jsonResponse, HttpServletResponse response) {
        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        int annoPubblicazione = Integer.parseInt(request.getParameter("anno_pubblicazione"));
        String genere = request.getParameter("genere");
        int copieDisponibili = Integer.parseInt(request.getParameter("copie_disponibili"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO libri (titolo, autore, anno_pubblicazione, genere, copie_disponibili) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, titolo);
                pstmt.setString(2, autore);
                pstmt.setInt(3, annoPubblicazione);
                pstmt.setString(4, genere);
                pstmt.setInt(5, copieDisponibili);
                pstmt.executeUpdate();
                jsonResponse.put("successo", true);
                jsonResponse.put("messaggio", "Libro aggiunto con successo.");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("successo", false);
            jsonResponse.put("messaggio", "Errore nell'aggiunta del libro.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        try {
            sendResponse(response, jsonResponse);
        } catch (IOException ex) {
            Logger.getLogger(GestioneLibriServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo per modificare un libro
    private void modificaLibro(HttpServletRequest request, JSONObject jsonResponse, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        Integer annoPubblicazione = request.getParameter("anno_pubblicazione").isEmpty() ? null : Integer.parseInt(request.getParameter("anno_pubblicazione"));
        String genere = request.getParameter("genere");
        Integer copieDisponibili = request.getParameter("copie_disponibili").isEmpty() ? null : Integer.parseInt(request.getParameter("copie_disponibili"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder query = new StringBuilder("UPDATE libri SET ");
            if (titolo != null) query.append("titolo = ?, ");
            if (autore != null) query.append("autore = ?, ");
            if (annoPubblicazione != null) query.append("anno_pubblicazione = ?, ");
            if (genere != null) query.append("genere = ?, ");
            if (copieDisponibili != null) query.append("copie_disponibili = ? ");
            query.append("WHERE id = ?");
            
            try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
                int index = 1;
                if (titolo != null) pstmt.setString(index++, titolo);
                if (autore != null) pstmt.setString(index++, autore);
                if (annoPubblicazione != null) pstmt.setInt(index++, annoPubblicazione);
                if (genere != null) pstmt.setString(index++, genere);
                if (copieDisponibili != null) pstmt.setInt(index++, copieDisponibili);
                pstmt.setInt(index, id);
                
                pstmt.executeUpdate();
                jsonResponse.put("successo", true);
                jsonResponse.put("messaggio", "Libro modificato con successo.");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("successo", false);
            jsonResponse.put("messaggio", "Errore nella modifica del libro.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        try {
            sendResponse(response, jsonResponse);
        } catch (IOException ex) {
            Logger.getLogger(GestioneLibriServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo per eliminare un libro
    private void eliminaLibro(HttpServletRequest request, JSONObject jsonResponse, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM libri WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                jsonResponse.put("successo", true);
                jsonResponse.put("messaggio", "Libro eliminato con successo.");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("successo", false);
            jsonResponse.put("messaggio", "Errore nell'eliminazione del libro.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        try {
            sendResponse(response, jsonResponse);
        } catch (IOException ex) {
            Logger.getLogger(GestioneLibriServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo per inviare la risposta JSON
    private void sendResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
}
