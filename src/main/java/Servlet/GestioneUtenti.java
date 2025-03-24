package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//per db
import Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "GestioneUtenti", urlPatterns = {"/GestioneUtenti"})
public class GestioneUtenti extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String azione = request.getParameter("azione"); // Valore nascosto nel form
        
        if (azione.equals("accedi")) {
            gestisciLogin(request, response);
        } else if (azione.equals("registrazione")) {
            gestisciRegistrazione(request, response);
        } else {
            response.sendRedirect("errore.jsp"); // Pagina di errore generica
        }
    }
    
    private void gestisciLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        String passwordDB = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT password FROM utenti WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                
                try (ResultSet rs = pstmt.executeQuery()){
                    if(rs.next()){
                        passwordDB = rs.getString("password");
                        
                        if(verificaPassword(password, passwordDB)){
                            //corretto
                            HttpSession session = request.getSession();
                            session.setAttribute("user", email);
                            request.getRequestDispatcher("home.jsp").forward(request, response);
                            response.sendRedirect("home.jsp");
                        }else{
                            response.sendRedirect("accedi.html?error='password errata'");
                        }
                    }
                }

                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void gestisciRegistrazione(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "INSERT INTO utenti (nome, email, password, ruolo) VALUES (?, ?, ?, 'utente')";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, criptaPassword(password));
            pstmt.executeUpdate();
            
            response.sendRedirect("accedi.html");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }   
    
    // Metodo per criptare la password
    public static String criptaPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Metodo per verificare la password
    public static boolean verificaPassword(String passwordInserita, String hashedPasswordFromDb) {
        return BCrypt.checkpw(passwordInserita, hashedPasswordFromDb);
    }
}
