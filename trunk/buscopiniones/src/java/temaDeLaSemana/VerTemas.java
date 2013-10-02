/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package temaDeLaSemana;

import buscopiniones.Noticia;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rodrigo
 */
public class VerTemas extends HttpServlet {

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
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			if (request.getParameter("desde") != null && !request.getParameter("desde").equals("")
					&& request.getParameter("hasta") != null && !request.getParameter("hasta").equals("")) {
				ProcesadorTemas procTemas = new ProcesadorTemas();
				Collection<Noticia> noticias = procTemas.getNoticiaDeLaSemana(request.getParameter("desde"), request.getParameter("hasta"));
//				FileInputStream fileIn = new FileInputStream("C:\\Fing\\ProyGrado\\tmpTemas\\salida.bin");
//				ObjectInputStream in = new ObjectInputStream(fileIn);
//				noticias = (Collection<Noticia>) in.readObject();
//				in.close();
//				fileIn.close();

				request.setAttribute("Noticias", noticias);
			}
			request.getRequestDispatcher("/VerTemas.jsp").forward(request, response);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			out.close();
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
