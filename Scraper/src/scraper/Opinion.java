package scraper;

/**
 *
 * @author Rodrigo
 */
public class Opinion {

	private Noticia noticia;
	private Fuente fuente;
	private Fuente fuente_corref;
	private String opinion;
	private String id;

	public Opinion(Noticia noti, Fuente fuente, String opinion, String id) {
		this.noticia = noti;
		this.fuente = fuente;
		this.opinion = opinion;
		this.id = id;
		this.fuente_corref = new Fuente("", "", "");
	}

	public String toXML() {
		String xml = "<doc>\r\n";
		xml += noticia.toXML();
		xml += "<field name=\"fuente\">" + ProcesadorHTML.html2text(fuente.getFuente()) + "</field>\r\n";
		xml += "<field name=\"fuente_corref\">" + ProcesadorHTML.html2text(getFuente_corref().getFuente()) + "</field>\r\n";
		xml += "<field name=\"opinion\">" + ProcesadorHTML.html2text(opinion) + "</field>\r\n";
		xml += "<field name=\"id\">" + noticia.getUrl() + "/" + id + "</field>\r\n";
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

	/**
	 * @return the fuente_corref
	 */
	public Fuente getFuente_corref() {
		return fuente_corref;
	}
}
