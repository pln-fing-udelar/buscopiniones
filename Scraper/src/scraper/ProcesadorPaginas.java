/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
		ProcesadorHTML proc = new ProcesadorHTML(html, url);
		Noticia noti = proc.procesar(medioDePrensa);
		coleccionNoticias.add(noti);
	}

	public String taggear() throws BoilerpipeProcessingException, IOException, ParserConfigurationException, SAXException {
		String xml = "";

		Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirFreeling() + "entradaFreeling.txt"), "utf8"));
		for (Noticia noti : coleccionNoticias) {
			bw.write(noti.getArticulo());
			bw.write("\r\n");
			bw.write("--------------------------------------------------------------");
			bw.write("\r\n");
		}
		bw.close();

		TaggerOpiniones tagger = new TaggerOpiniones(config.getDirOpiniones(), config.getDirFreeling(), config.getDirProlog());
		//config.getDirOpiniones() + "entrada"
		tagger.taggearFreelingDesdeArchivo(config.getDirFreeling() + "entradaFreeling.txt", config.getDirFreeling() + "salidaFreeling.txt");
		String salidaFreeling = Main.readFile(config.getDirFreeling() + "salidaFreeling.txt", "utf8");
//		System.out.println(salidaFreeling);
		String[] arrFreeling = salidaFreeling.split("(?m)-------------------------------------------------------------- -------------------------------------------------------------- Fz 1");

		int i = 0;
		for (Noticia noti : coleccionNoticias) {
			//i = salidaFreeling.indexOf("--------------------------------------------------------------", i+1);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirOpiniones() + "entrada"), "Windows-1252"));

			bw.write(arrFreeling[i++]);
			bw.flush();
			bw.close();
			tagger.taggearOpiniones();

			CopyFiles.copyWithChannels(config.getDirOpiniones() + "salida", config.getDirCorreferencias() + "entrada.xml", false);

			TaggerCorreferencias taggerCorref = new TaggerCorreferencias(config);
			taggerCorref.taggearCorreferencias();
			//---<borrar>
//			java.util.Date date = new java.util.Date();
//			long unixTime = System.currentTimeMillis() / 1000L;
//			CopyFiles.copyWithChannels(config.getDirCorreferencias() + "entrada.xml", "C:\\Fing\\ProyGrado\\basura\\" + unixTime + "entrada.xml", false);
//			CopyFiles.copyWithChannels(config.getDirCorreferencias() + "salidaFinal.xml", "C:\\Fing\\ProyGrado\\basura\\" + unixTime + "salidaFinal.xml", false);
//			CopyFiles.copyWithChannels(config.getDirCorreferencias() + "salidaRec.xml", "C:\\Fing\\ProyGrado\\basura\\" + unixTime + "salidaRec.xml", false);
			//---</borrar>

			Collection<Opinion> opiniones = taggerCorref.obtenerOpiniones(noti);
			for (Opinion op : opiniones) {
				xml += op.toXML();
			}
		}
		coleccionNoticias = new ArrayList<Noticia>();
		return xml;
	}
}
