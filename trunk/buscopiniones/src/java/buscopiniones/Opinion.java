package buscopiniones;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Rodrigo
 */
public class Opinion {

	private Noticia noticia;
	private String fuente;
	private String opinion;
	private String id;

	public Opinion() {
	}

	public Opinion(Noticia noti, String fuente, String opinion, String id) {
		this.noticia = noti;
		this.fuente = fuente;
		this.opinion = opinion;
		this.id = id;
	}

	private String getFechaParaJSON() {
		String fecha = noticia.getFecha();
		Pattern p = Pattern.compile("(?i)([0-9][0-9][0-1][0-9])-([0-1][0-9])-([0-3][0-9])");
		Matcher m = p.matcher(fecha);

		if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(3)) <= 31)) {
			fecha = m.group(1) + "," + m.group(2) + "," + m.group(3);
		}
		return fecha;
	}

	public String toJSON() {
		String opinionJson = BuscadorOpiniones.html2text(this.getOpinion());
		String tituloJson = BuscadorOpiniones.html2text(noticia.getTitle());
		String urlJson = BuscadorOpiniones.html2text(noticia.getUrl());
		BASE64Encoder encoder = new BASE64Encoder();
		String base64 = encoder.encode(noticia.getUrl().getBytes()).replaceAll("\r\n", "");
		System.out.println("toy aca");
		System.out.println(base64);
		String media = "http://localhost:8084/buscopiniones/ImagenNoticia/" + base64 + ".jpg";
//		String media = "http://localhost:8084/buscopiniones/ImagenNoticia/aHR0cDovL2hpc3Rvcmljby5lbHBhaXMuY29tLnV5LzEyMDYyNS91bHRtby02NDgyNjcvdWx0aW1vbW9tZW50by9NZWRpZGFzLXNvYnJlLWxhLW1hcmlodWFuYS1zb24tcGFyYS1wcm90ZWdlci1hbC1jb25zdW1pZG9yLWRpam8tTXVqaWNhLw==.jpg";
		String credit = "";
		if (opinionJson.matches(".*?&quot;.*?&quot;.*?")) {
			media = opinionJson.replaceAll(".*?&quot;(.*?)&quot;.*?", "<blockquote>&quot;$1&quot;</blockquote>");
			credit = this.getFuente();
		}

		String json = "{";
		json += "\"startDate\":\"" + getFechaParaJSON() + "\",";
		json += "\"headline\":\"" + tituloJson + "\",";
		json += "\"text\":\"<p>" + opinionJson + "</p><p>" + urlJson + "</p>\",";
		json += "\"asset\":{\n"
				+ "                    \"media\":\"" + media + "\",\n"
				+ "                    \"credit\":\"" + credit + "\",\n"
				+ "                    \"caption\":\"\"\n"
				+ "                }";
		json += "}";

		return json;
	}

	/**
	 * @return the noticia
	 */
	public Noticia getNoticia() {
		return noticia;
	}

	/**
	 * @return the fuente
	 */
	public String getFuente() {
		return fuente;
	}

	/**
	 * @return the opinion
	 */
	public String getOpinion() {
		return opinion;
	}

	/**
	 * @param noticia the noticia to set
	 */
	public void setNoticia(Noticia noticia) {
		this.noticia = noticia;
	}

	/**
	 * @param fuente the fuente to set
	 */
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}

	/**
	 * @param opinion the opinion to set
	 */
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
