/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

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
		return getTamanioTotalHTML().toString() + coma + getLargoUrl().toString() + coma + getCantH1().toString() + coma
				+ getCantH2().toString() + coma + getCantH3().toString() + coma + getCantH4().toString()
				+ coma + getCantH5().toString() + coma + getCantDiv().toString() + coma + getCantTags().toString()
				+ coma + getEsArticulo().toString();
	}
	
	static void guardarCSV(String archivo, List<Ejemplo> ejemplos) {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(archivo, false), Charset.forName("ISO-8859-15"));

			out.write("Tamanio, LargoUrl, CantH1, CantH2, CantH3, CantH4,"
					+ " CantH5, CantDiv, CantTags, EsArticulo");
			out.write(System.getProperty("line.separator"));

			Iterator it = ejemplos.iterator();
			while (it.hasNext()) {
				out.write(((Ejemplo) it.next()) + "");
				out.write(System.getProperty("line.separator"));
			}
			out.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * @return the tamanioTotalHTML
	 */
	public Integer getTamanioTotalHTML() {
		return tamanioTotalHTML;
	}

	/**
	 * @return the largoUrl
	 */
	public Integer getLargoUrl() {
		return largoUrl;
	}

	/**
	 * @return the cantH1
	 */
	public Integer getCantH1() {
		return cantH1;
	}

	/**
	 * @return the cantH2
	 */
	public Integer getCantH2() {
		return cantH2;
	}

	/**
	 * @return the cantH3
	 */
	public Integer getCantH3() {
		return cantH3;
	}

	/**
	 * @return the cantH4
	 */
	public Integer getCantH4() {
		return cantH4;
	}

	/**
	 * @return the cantH5
	 */
	public Integer getCantH5() {
		return cantH5;
	}

	/**
	 * @return the cantDiv
	 */
	public Integer getCantDiv() {
		return cantDiv;
	}

	/**
	 * @return the cantTags
	 */
	public Integer getCantTags() {
		return cantTags;
	}

	/**
	 * @return the esArticulo
	 */
	public Boolean getEsArticulo() {
		return esArticulo;
	}
	
}
