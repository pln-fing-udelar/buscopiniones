/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscopiniones;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
			String fechaIni = request.getParameter("desde");
			String fechaFin = request.getParameter("hasta");
			String medioDePrensa = request.getParameter("medioDePrensa");
			String cantResultados = request.getParameter("cantResultados");
			BuscadorOpiniones buscador = new BuscadorOpiniones();
			Collection<String> fuentes = buscador.getFuentesRelacionadas(fuente, asunto, fechaIni, fechaFin);
			StringBuilder spellCheckFuente = new StringBuilder();
			StringBuilder spellCheckAsunto = new StringBuilder();
			buscador.getSpellCheckFuenteAsunto(fuente, asunto, spellCheckFuente, spellCheckAsunto);
			System.out.println("spellCheckFuente:" + spellCheckFuente);
			System.out.println("spellCheckAsunto:" + spellCheckAsunto);

			int start_at_slide = 0;
			if (!(fuente == null || fuente.isEmpty() || fuente.equals("null") || asunto == null || asunto.isEmpty() || asunto.equals("null"))) {
				ArrayList<Opinion> opiniones = buscador.getOpiniones(fuente, asunto, fechaIni, fechaFin, medioDePrensa, cantResultados);
				if (opiniones.size() > 0) {
					Opinion op = opiniones.get(0);					
					Collections.sort(opiniones, new ComparadorOpiniones());
					start_at_slide = 1;
					for(Opinion op2 : opiniones){
						System.out.println("op2:"+op2.getId());
						System.out.println("op:"+op.getId());
						System.out.println("start_at_slide:"+start_at_slide);
						if(op2.getId().equals(op.getId())){
							break;
						}
						start_at_slide++;
					}
						
				}
			}
			request.setAttribute("start_at_slide", start_at_slide);
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
