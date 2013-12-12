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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
			if (request.getParameter("semana") != null && !request.getParameter("semana").equals("")) {
				String input = request.getParameter("semana");
				String format = "dd/MM/yyyy";

				SimpleDateFormat df = new SimpleDateFormat(format);
				Date date = df.parse(input);

				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int week = cal.get(Calendar.WEEK_OF_YEAR);
				int anio = cal.get(Calendar.YEAR);

				String filePath = "C:\\Fing\\ProyGrado\\tmpTemas\\" + anio + "_" + week + ".bin"; // C:\\Fing\\ProyGrado\\tmpTemas\\ /usr/share/tomcat6/tmpTemas/
				FileInputStream fileIn = new FileInputStream(filePath);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				Collection<Noticia> noticias = (Collection<Noticia>) in.readObject();
				in.close();
				fileIn.close();
				request.setAttribute("Noticias", noticias);
				
				Calendar caldesde = Calendar.getInstance();
				caldesde.setTime(date);
				caldesde.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				String desde = df.format(caldesde.getTime());
				request.setAttribute("desde", desde);
				
				Calendar calhasta = Calendar.getInstance();
				calhasta.setTime(date);
				calhasta.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				String hasta = df.format(calhasta.getTime());
				request.setAttribute("hasta", hasta);
			}
			if (request.getParameter("procesarAnio") != null && !request.getParameter("procesarAnio").equals("")) {
				int limiteSemana = 52; // un anio tiene 52 semanas
				if (request.getParameter("limiteSemana") != null && !request.getParameter("limiteSemana").equals("")) {
					limiteSemana = Integer.parseInt(request.getParameter("limiteSemana"));
				}
				ProcesadorTemas procTemas = new ProcesadorTemas();
				String format = "dd/MM/yyyy";
				SimpleDateFormat df = new SimpleDateFormat(format);
				for (int i = 1; i <= limiteSemana; i++) {					
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, Integer.parseInt(request.getParameter("procesarAnio")));
					cal.set(Calendar.WEEK_OF_YEAR, i);
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					String desde = df.format(cal.getTime());

					cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, Integer.parseInt(request.getParameter("procesarAnio")));
					cal.set(Calendar.WEEK_OF_YEAR, i);
					cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					String hasta = df.format(cal.getTime());
					
					System.out.println("procesarAniodesde:"+desde);
					System.out.println("procesarAniohasta:"+hasta);
					Collection<Noticia> noticias = procTemas.getNoticiaDeLaSemana(desde, hasta);
				}
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
