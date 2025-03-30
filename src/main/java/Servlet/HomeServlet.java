package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.json.JSONObject;
import Database.DatabaseConnection;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("accedi.html");
            return;
        }

        String email = (String) session.getAttribute("user");
        JSONObject jsonResponse = new JSONObject();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nome, ruolo FROM utenti WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        jsonResponse.put("nome", rs.getString("nome"));
                        jsonResponse.put("ruolo", rs.getString("ruolo"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("error", "Errore nel database");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
