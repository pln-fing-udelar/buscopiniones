package scraper;

/**
 *
 * @author Rodrigo
 */
public class Opinion {

	private Noticia noticia;
	private Fuente fuente;
	private String opinion;

	public Opinion(Noticia noti, Fuente fuente, String opinion) {
		this.noticia = noti;
		this.fuente = fuente;
		this.opinion = opinion;
	}

	public String toXML() {
		String xml = "<doc>\r\n";
		xml += noticia.toXML();
		xml += "<field name=\"fuente\">" + fuente.getFuente() + "</field>\r\n";
		xml += "<field name=\"opinion\">" + opinion + "</field>\r\n";
		xml += "</doc>\r\n";
		return xml;
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
	public Fuente getFuente() {
		return fuente;
	}

	/**
	 * @return the opinion
	 */
	public String getOpinion() {
		return opinion;
	}
}
