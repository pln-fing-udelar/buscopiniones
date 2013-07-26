package buscopiniones;

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

	public Noticia(){
		
	}
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
		xml += "<field name=\"url\">" + url + "</field>\r\n";
		xml += "<field name=\"articulo\">" + articulo + "</field>\r\n";
		xml += "<field name=\"title\">" + title + "</field>\r\n";
		xml += "<field name=\"metatitle\">" + metatitle + "</field>\r\n";
		xml += "<field name=\"h1\">" + h1 + "</field>\r\n";
		xml += "<field name=\"fecha\">" + fecha + "</field>\r\n";
		xml += "<field name=\"categorias\">" + categorias + "</field>\r\n";
		xml += "<field name=\"descripcion\">" + getDescripcion() + "</field>\r\n";
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

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param articulo the articulo to set
	 */
	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param metatitle the metatitle to set
	 */
	public void setMetatitle(String metatitle) {
		this.metatitle = metatitle;
	}

	/**
	 * @param h1 the h1 to set
	 */
	public void setH1(String h1) {
		this.h1 = h1;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * @param categorias the categorias to set
	 */
	public void setCategorias(String categorias) {
		this.categorias = categorias;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @param autor the autor to set
	 */
	public void setAutor(String autor) {
		this.autor = autor;
	}
}
