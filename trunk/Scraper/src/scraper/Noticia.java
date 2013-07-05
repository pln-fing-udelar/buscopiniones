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
		xml += "<field name=\"url\">" + url + "</url>\r\n";
		xml += "<field name=\"articulo\">" + articulo + "</articulo>\r\n";
		xml += "<field name=\"title\">" + title + "</title>\r\n";
		xml += "<field name=\"metatitle\">" + metatitle + "</metatitle>\r\n";
		xml += "<field name=\"h1\">" + h1 + "</h1>\r\n";
		xml += "<field name=\"fecha\">" + fecha + "</fecha>\r\n";
		xml += "<field name=\"categorias\">" + categorias + "</categorias>\r\n";
		xml += "<field name=\"descripcion\">" + getDescripcion() + "</descripcion>\r\n";
		xml += "<field name=\"autor\">" + getAutor() + "</autor>\r\n";
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
