package buscopiniones;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String json = "{";
		json += "\"startDate\":\"" + getFechaParaJSON() + "\",";
		json += "\"headline\":\"" + BuscadorOpiniones.html2text(noticia.getTitle()) + "\",";
		json += "\"text\":\"<p>" + BuscadorOpiniones.html2text(this.getOpinion()) + "</p>\",";
		json += "\"asset\":{\n"
				+ "                    \"media\":\"\",\n"
				+ "                    \"credit\":\"\",\n"
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
