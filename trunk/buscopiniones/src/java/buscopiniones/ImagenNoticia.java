/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscopiniones;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Rodrigo
 */
@WebServlet(name = "ImagenNoticia", urlPatterns = {"/ImagenNoticia"})
public class ImagenNoticia extends HttpServlet {

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
	public BufferedImage obtenerImagenDeURL(String urlString, HttpSession session) throws MalformedURLException, IOException, Exception {
		System.out.println("toy aca 1");
		BufferedImage imagenRet = null;
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
		Matcher m = p.matcher(con.getContentType());
		/* If Content-Type doesn't match this pre-conception, choose default and 
		 * hope for the best. */
		String charset = m.matches() ? m.group(1) : "ISO-8859-1";
		Reader r = new InputStreamReader(con.getInputStream(), charset);
		StringBuilder buf = new StringBuilder();
		System.out.println("toy aca 2");
		while (true) {
			int ch = r.read();
			if (ch < 0) {
				break;
			}
			buf.append((char) ch);
		}
		String str = buf.toString();
		System.out.println("toy aca 3");
		p = Pattern.compile("src=(\"|')([^\"']*?\\.(jpg|gif|png|JPG|PNG|GIF))(\"|')");
		m = p.matcher(str);
		int max = 0;
		String idImg = Math.random() + "";
		session.setAttribute(idImg + "max", 0);
		ExecutorService pool = Executors.newFixedThreadPool(20);
		while (m.find()) {
			String imagenCandidata = m.group(2);
			if (imagenCandidata != null && !imagenCandidata.matches("http://.*")) {
				imagenCandidata = urlString.replaceFirst("(http://.*?)/.*", "$1") + imagenCandidata;
			}
			System.out.println("imagenCandidata: " + imagenCandidata);
			if (imagenCandidata != null) {
				pool.submit(new DownloadTask(imagenCandidata, idImg, session));
			}
		}
		pool.shutdown();
		pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		System.out.println("toy aca 4");
//		imagenRet = DownloadTask.getMejorImagen();
		imagenRet = (BufferedImage) session.getAttribute(idImg + "img");
		if (imagenRet == null) {
			throw new Exception("No encontre ninguna imagen");
		}
		return imagenRet;
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("image/jpg");
		OutputStream out = response.getOutputStream();
		HttpSession session = request.getSession();

		try {
			BASE64Decoder decoder = new BASE64Decoder();
			String pathInfo = request.getPathInfo();
			String[] pathParts = pathInfo.split("/");

			byte[] decodedBytes = decoder.decodeBuffer(pathParts[1].replaceAll("\\.jpg$", ""));
			String url = new String(decodedBytes);
			BufferedImage img = null;
			img = obtenerImagenDeURL(url, session);
			if (img != null) {
				ImageIO.write(img, "jpg", out);
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println(e.getCause());
			BufferedImage img = ImageIO.read(new File("C:\\Fing\\ProyGrado\\buscopiniones\\buscopiniones\\web\\img\\default.jpg"));
			ImageIO.write(img, "jpg", out);
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
