package it.unisa.control;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.unisa.model.*;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
			
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	UserDao usDao = new UserDao();
		
		try
		{	    

			String username = request.getParameter("un");
	        String password = request.getParameter("pw");

	        // Hashing della password
	        String hashedPassword = hashPassword(password);

	        UserBean user = usDao.doRetrieve(username, hashedPassword);

	        String checkout = request.getParameter("checkout");

	        if (user.isValid()) {
	            HttpSession session = request.getSession(true);
	            session.setAttribute("currentSessionUser", user);
	            if (checkout != null)
	                response.sendRedirect(request.getContextPath() + "/account?page=Checkout.jsp");
	            else
	                response.sendRedirect(request.getContextPath() + "/Home.jsp");
	        } else {
	            response.sendRedirect(request.getContextPath() + "/Login.jsp?action=error"); //error page
	        }
	    } catch (SQLException e) {
	        System.out.println("Error:" + e.getMessage());
	    } catch (NoSuchAlgorithmException e) {
	        System.out.println("Error while hashing the password: " + e.getMessage());
	    }
	}

	// Metodo per hashare la password utilizzando SHA-256
	private String hashPassword(String password) throws NoSuchAlgorithmException {
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

	    StringBuilder hexString = new StringBuilder();
	    for (byte b : hash) {
	        String hex = Integer.toHexString(0xff & b);
	        if (hex.length() == 1) hexString.append('0');
	        hexString.append(hex);
	    }

	    return hexString.toString();
	}
