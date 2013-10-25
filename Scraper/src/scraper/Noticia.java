package scraper;

/**
 *
 * @author Rodrigo
 */
public class Noticia {

	private String url;
	private String articulo;
	private String title;
	private String metatitle;
	private String h1;
	private String fecha;
	private String categorias;
	private String descripcion;
	private String autor;

	public Noticia(String url, String articulo, String title, String metatitle, String h1, String fecha, String categorias, String descripcion, String autor) {
		this.url = url;
		this.articulo = articulo;
		this.title = title;
		this.metatitle = metatitle;
		this.h1 = h1;
		this.fecha = fecha;
		this.categorias = categorias;
		this.descripcion = descripcion;
		this.autor = autor;
	}

	public String toXML() {
		String xml = "";
		xml += "<field name=\"url\">" + ProcesadorHTML.html2text(url) + "</field>\r\n";
		xml += "<field name=\"articulo\">" + ProcesadorHTML.html2text(articulo) + "</field>\r\n";
		xml += "<field name=\"title\">" + title + "</field>\r\n";
		xml += "<field name=\"metatitle\">" + metatitle + "</field>\r\n";
		xml += "<field name=\"h1\">" + h1 + "</field>\r\n";
		xml += "<field name=\"fecha\">" + fecha + "</field>\r\n";
		xml += "<field name=\"categorias\">" + categorias + "</field>\r\n";
		xml += "<field name=\"descripcion\">" + ProcesadorHTML.html2text(getDescripcion()) + "</field>\r\n";
		xml += "<field name=\"autor\">" + getAutor() + "</field>\r\n";
		return xml;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the articulo
	 */
	public String getArticulo() {
		return articulo;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the metatitle
	 */
	public String getMetatitle() {
		return metatitle;
	}

	/**
	 * @return the h1
	 */
	public String getH1() {
		return h1;
	}

	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * @return the categorias
	 */
	public String getCategorias() {
		return categorias;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @return the autor
	 */
	public String getAutor() {
		return autor;
	}
}
