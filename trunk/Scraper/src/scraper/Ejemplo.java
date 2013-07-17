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
		return tamanioTotalHTML.toString() + coma + largoUrl.toString() + coma + cantH1.toString() + coma
				+ cantH2.toString() + coma + cantH3.toString() + coma + cantH4.toString()
				+ coma + cantH5.toString() + coma + cantDiv.toString() + coma + cantTags.toString()
				+ coma + esArticulo.toString();
	}
	
	static void guardarCSV(String archivo, List<Ejemplo> ejemplos) {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(archivo, false), Charset.forName("ISO-8859-15"));

			out.write("Tamanio, largoUrl, cantH1, cantH2, cantH3, cantH4,"
					+ " cantH5, cantDiv, cantTags, esArticulo");
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
	
}
