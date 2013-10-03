/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscopiniones;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rodrigo
 */
@WebServlet(name = "Index", urlPatterns = {"/Index"})
public class Home extends HttpServlet {

	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		try {
			String fuente = request.getParameter("fuente");
			String asunto = request.getParameter("asunto");
			BuscadorOpiniones buscador = new BuscadorOpiniones();
			Collection<String> fuentes = buscador.getFuentesRelacionadas(fuente, asunto, null, null);
			StringBuilder spellCheckFuente = new StringBuilder();
			StringBuilder spellCheckAsunto = new StringBuilder();
			buscador.getSpellCheckFuenteAsunto(fuente, asunto, spellCheckFuente, spellCheckAsunto);
			System.out.println("spellCheckFuente:" + spellCheckFuente);
			System.out.println("spellCheckAsunto:" + spellCheckAsunto);
			request.setAttribute("fuentes", fuentes);
			request.setAttribute("spellCheckFuente", spellCheckFuente.toString());
			request.setAttribute("spellCheckAsunto", spellCheckAsunto.toString());
			request.getRequestDispatcher("/Home.jsp").forward(request, response);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP
	 * <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP
	 * <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
