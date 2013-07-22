/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

/**
 *
 * @author Bongo
 */
public class FiltradorPaginas {
	
	private String url;
	private String html;
	private String medioDePrensa;
	
	FiltradorPaginas(ProcesadorHTML proc, String medioDePrensa) {
		url = proc.getUrl();
		html = proc.getHtml();
		this.medioDePrensa = medioDePrensa;
	}
	int largoUrl(){
		return url.length();
	}

	boolean pasaFiltro() {
		if (medioDePrensa.equals("elobservador")) {
			return largoUrl() > 46 && url.contains("noticia");
		} else {
			return true;
		}
	}
}	
