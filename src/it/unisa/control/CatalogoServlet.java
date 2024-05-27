import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.unisa.model.ProdottoBean;
import it.unisa.model.ProdottoDao;




@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {
	
	
    private static final long serialVersionUID = 1L;
    
    
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Rimuove i tag HTML
        input = input.replaceAll("\\<.*?\\>", "");
        System.out.println(input);
        
        // Rimuove i caratteri speciali tranne spazi, lettere e numeri
        input = input.replaceAll("[^a-zA-Z0-9\\s]", "");
        
        return input;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = sanitizeInput(request.getParameter("action"));
        String redirectedPage = sanitizeInput(request.getParameter("page"));

        // Lista delle pagine valide
        List<String> validPages = Arrays.asList("Account.jsp", "Carrello.jsp", "Catalogo.jsp", "Checkout.jsp", 
                                                "ComposizioneOrdine.jsp", "Dettagli.jsp", "Home.jsp", "Login.jsp", 
                                                "MieiOrdini.jsp", "Ps4.jsp", "Ps5.jsp", "Registrazione.jsp", 
                                                "Switch.jsp", "XboxOne.jsp", "XboxSeries.jsp");

        // Verifica se la pagina rediretta è valida, altrimenti reindirizza alla Home
        if (!validPages.contains(redirectedPage)) {
            redirectedPage = "Home.jsp";
        }

        // Reindirizza alla pagina valida o restituisce errore se la pagina non è valida
        request.getRequestDispatcher("/" + redirectedPage).forward(request, response);

        ProdottoDao prodDao = new ProdottoDao();
        ProdottoBean bean = new ProdottoBean();

        try {
            if (action != null) {
                if (action.equalsIgnoreCase("add")) {
                    // Popola il bean con i parametri del nuovo prodotto
                    populateBean(request, bean);
                    prodDao.doSave(bean);
                } else if (action.equalsIgnoreCase("modifica")) {
                    // Popola il bean con i parametri del prodotto da modificare
                    populateBean(request, bean);
                    prodDao.doUpdate(bean);
                }
                // Rimuovi l'attributo "categorie" dalla sessione
                request.getSession().removeAttribute("categorie");
            }

            // Aggiorna la sessione con la lista aggiornata di prodotti
            request.getSession().setAttribute("products", prodDao.doRetrieveAll(sanitizeInput(request.getParameter("sort"))));
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }

        // Reindirizza alla pagina corretta
        response.sendRedirect(request.getContextPath() + "/" + redirectedPage);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // Metodo per popolare il bean ProdottoBean con i parametri della richiesta
    private void populateBean(HttpServletRequest request, ProdottoBean bean) {
        bean.setIdProdotto(Integer.parseInt(sanitizeInput(request.getParameter("id"))));
        bean.setNome(sanitizeInput(request.getParameter("nome")));
        bean.setDescrizione(sanitizeInput(request.getParameter("descrizione")));
        bean.setIva(sanitizeInput(request.getParameter("iva")));
        bean.setPrezzo(Double.parseDouble(sanitizeInput(request.getParameter("prezzo"))));
        bean.setQuantità(Integer.parseInt(sanitizeInput(request.getParameter("quantità"))));
        bean.setPiattaforma(sanitizeInput(request.getParameter("piattaforma")));
        bean.setGenere(sanitizeInput(request.getParameter("genere")));
        bean.setImmagine(sanitizeInput(request.getParameter("img")));
        bean.setDataUscita(sanitizeInput(request.getParameter("dataUscita")));
        bean.setDescrizioneDettagliata(sanitizeInput(request.getParameter("descDett")));
        bean.setInVendita(true);
    }

    


}

