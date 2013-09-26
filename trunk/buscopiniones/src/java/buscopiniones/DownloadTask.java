/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscopiniones;

import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Rodrigo
 */
public class DownloadTask implements Runnable {

	private String imagenCandidata;
	private String idImg;
	private HttpSession session;
	private static BufferedImage mejorImagen = null;
	private static int max = 0;
	private static int maxHeight = 200;
	private static int maxWidth = 200;
	private static double maxProporcion = 2.0;
	private static double minProporcion = 0.3;

	public DownloadTask(String imagenCandidata, String idImg, HttpSession session) {
		this.imagenCandidata = imagenCandidata;
		this.idImg = idImg;
		this.session = session;
	}

	public static BufferedImage getMejorImagen() {
		return mejorImagen;
	}

	public static synchronized void probarMejorImagen(BufferedImage img, String idImg, HttpSession session) {
		if (img != null) {
			int height = img.getHeight();
			int width = img.getWidth();
			double proporcion = ((double) height) / ((double) width);
			System.out.println("height: " + height);
			System.out.println("width: " + width);
			System.out.println("proporcion: " + proporcion);
			if (((Integer)session.getAttribute(idImg+ "max")) <= (height * width) && (height * width) >= (maxHeight * maxWidth) && proporcion < maxProporcion && proporcion > minProporcion) {
				session.setAttribute(idImg + "img", img);
				session.setAttribute(idImg + "max", (height * width));
				mejorImagen = img;
				max = height * width;
			}
		}
	}

	@Override
	public void run() {
		try {
			BufferedImage img = ImageIO.read(new URL(imagenCandidata));
			probarMejorImagen(img, idImg, session);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println(e.getCause());
		}
	}
}
