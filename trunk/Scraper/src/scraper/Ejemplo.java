/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

/**
 *
 * @author Bongo
 */
public class Ejemplo {
	
	private Integer tamanioTotalHTML;
	private Integer largoUrl;
	private Integer cantH1;
	private Integer cantH2;
	private Integer cantH3;
	private Integer cantH4;
	private Integer cantH5;
	private Integer cantDiv;
	private Integer cantTags;
	private Boolean esArticulo;

	Ejemplo (ProcesadorHTML pagina, boolean esArticulo) {
		tamanioTotalHTML = pagina.calcularTamanioHTML();
		largoUrl = pagina.calcularLargoUrl();
		cantH1 = pagina.calcularH1();
		cantH2 = pagina.calcularH2();
		cantH3 = pagina.calcularH3();
		cantH4 = pagina.calcularH4();
		cantH5 = pagina.calcularH5();
		cantDiv = pagina.calcularDiv();
		cantTags = pagina.cantidadTagsHTML();
		this.esArticulo = esArticulo;
	}
	


	@Override
	public String toString(){
		String coma = ",";
		return tamanioTotalHTML.toString() + coma + largoUrl.toString() + coma + cantH1.toString() + coma
				+ cantH2.toString() + coma + cantH3.toString() + coma + cantH4.toString()
				+ coma + cantH5.toString() + coma + cantDiv.toString() + coma + cantTags.toString()
				+ coma + esArticulo.toString();
	}
	
}
