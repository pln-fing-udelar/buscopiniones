package scraper;

/**
 *
 * @author Rodrigo
 */
public class Fuente {

	private String fuente;
	private String id;
	private String idRec;

	public Fuente(String fuente, String id, String idRec) {
		this.fuente = fuente;
		this.id = id;
		this.idRec = idRec;
	}

	/**
	 * @return the fuente
	 */
	public String getFuente() {
		return fuente;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the idRec
	 */
	public String getIdRec() {
		return idRec;
	}

	/**
	 * @param fuente the fuente to set
	 */
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}
}
