/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodrigo
 */
public class ProcesadorPaginas {

	private Configuracion config;
	private String medioDePrensa;
	private Collection<Noticia> coleccionNoticias;

	public ProcesadorPaginas(Configuracion config, String medioDePrensa) {
		this.config = config;
		this.medioDePrensa = medioDePrensa;
		this.coleccionNoticias = new ArrayList<Noticia>();
	}

	public void procesar(String html, String url) throws BoilerpipeProcessingException, IOException, ParserConfigurationException, SAXException {
		System.out.println("Empiezo a procesar HTML");
		ProcesadorHTML proc = new ProcesadorHTML(html, url);		
		Noticia noti = proc.procesar(medioDePrensa);
		coleccionNoticias.add(noti);
	}

	public String taggear() throws BoilerpipeProcessingException, IOException, ParserConfigurationException, SAXException {
		String xml = "";
		System.out.println("Empiezo a procesar taggeo");
		Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirFreeling() + "entradaFreeling.txt"), "utf8"));
		for (Noticia noti : coleccionNoticias) {
			bw.write(noti.getArticulo());
			bw.write("\r\n");
			bw.write("--------------------------------------------------------------");
			bw.write("\r\n");
		}
		bw.close();
		
		TaggerOpiniones tagger = new TaggerOpiniones(config.getDirOpiniones(), config.getDirFreeling(), config.getDirProlog());
		System.out.println("Empiezo a taggear con freeling");
		tagger.taggearFreelingDesdeArchivo(config.getDirFreeling() + "entradaFreeling.txt", config.getDirFreeling() + "salidaFreeling.txt");

		String[] arrFreeling = null;
		int contador = 0;
		while (arrFreeling == null || ((arrFreeling.length - 1) != coleccionNoticias.size() && contador < 10)) {
			String salidaFreeling = Main.readFile(config.getDirFreeling() + "salidaFreeling.txt", "utf8");
			arrFreeling = salidaFreeling.split("(?m)-------------------------------------------------------------- -------------------------------------------------------------- Fz 1");
			contador++;
		}
		System.out.println("Termine con freeling");
		if (contador < 10) {
			int i = 0;
			for (Noticia noti : coleccionNoticias) {
				//i = salidaFreeling.indexOf("--------------------------------------------------------------", i+1);
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirOpiniones() + "entrada"), "Windows-1252"));

				bw.write(arrFreeling[i++]);
				bw.flush();
				bw.close();
				System.out.println("Empiezo a taggear opiniones");
				tagger.taggearOpiniones();

				CopyFiles.copyWithChannels(config.getDirOpiniones() + "salida", config.getDirCorreferencias() + "entrada.xml", false);

				TaggerCorreferencias taggerCorref = new TaggerCorreferencias(config);
				System.out.println("Empiezo a taggear correferencias");
				taggerCorref.taggearCorreferencias();
				//---<borrar>
//			java.util.Date date = new java.util.Date();
//			long unixTime = System.currentTimeMillis() / 1000L;
//			CopyFiles.copyWithChannels(config.getDirCorreferencias() + "entrada.xml", "C:\\Fing\\ProyGrado\\basura\\" + unixTime + "entrada.xml", false);
//			CopyFiles.copyWithChannels(config.getDirCorreferencias() + "salidaFinal.xml", "C:\\Fing\\ProyGrado\\basura\\" + unixTime + "salidaFinal.xml", false);
//			CopyFiles.copyWithChannels(config.getDirCorreferencias() + "salidaRec.xml", "C:\\Fing\\ProyGrado\\basura\\" + unixTime + "salidaRec.xml", false);
				//---</borrar>

				System.out.println("Empiezo a scrapear opiniones");
				Collection<Opinion> opiniones = taggerCorref.obtenerOpiniones(noti);
				for (Opinion op : opiniones) {
					xml += op.toXML();
				}
			}
			System.out.println("Termine con el taggeo!");
		}
		coleccionNoticias = new ArrayList<Noticia>();
		return xml;
	}
}
