package scraper;

/**
 *
 * @author Rodrigo
 */
public class Opinion {
	private Noticia noticia;
	private String fuente;
	private String opinion;

	public Opinion(Noticia noti, String fuente, String opinion){
		this.noticia = noti;
		this.fuente = fuente;
		this.opinion = opinion;
	}

	/**
	 * @return the noticia
	 */ public Noticia getNoticia() {
		return noticia;
	}

	/**
	 * @return the fuente
	 */ public String getFuente() {
		return fuente;
	}

	/**
	 * @return the opinion
	 */ public String getOpinion() {
		return opinion;
	}
}
