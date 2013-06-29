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
		System.out.println(salidaFreeling);
		String[] arrFreeling = salidaFreeling.split("(?m)-------------------------------------------------------------- -------------------------------------------------------------- Fz 1");
		System.out.println("toy aca freeling!");
//		System.out.println(Arrays.toString(arrFreeling));
		System.out.println(salidaFreeling);
//		System.exit(1);
		int i = 0;
		for (Noticia noti : coleccionNoticias) {
			//i = salidaFreeling.indexOf("--------------------------------------------------------------", i+1);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirOpiniones() + "entrada"), "Windows-1252"));
//			System.out.println(i +" freeling_aca");
//			System.out.println(noti.getArticulo());
//			System.out.println(i +" freeling_aca2");
//			System.out.println(arrFreeling[i]);
			bw.write(arrFreeling[i++]);
			bw.flush();
			bw.close();
			tagger.taggearOpiniones();

			CopyFiles.copyWithChannels(config.getDirOpiniones() + "salida", config.getDirCorreferencias() + "entrada.xml", false);
			//---<borrar>
//			java.util.Date date = new java.util.Date();
//			long unixTime = System.currentTimeMillis() / 1000L;
//			CopyFiles.copyWithChannels(config.getDirOpiniones() + "salida", "C:\\Fing\\ProyGrado\\basura\\entrada" + unixTime + ".xml", false);
			//---</borrar>
			TaggerCorreferencias tagger2 = new TaggerCorreferencias(config.getDirCorreferencias(), config.getDirPython());
			tagger2.taggearCorreferencias();

//		String salidaCorref = Main.readFile(config.getDirCorreferencias() + "salidaFinal.xml", "utf8");

			File fXmlFile = new File(config.getDirCorreferencias() + "salidaFinal.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("opinion");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				xml += "<elem>\r\n";
				xml += noti.toXML();
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String opinion = eElement.getTextContent();
					xml += "<opinion>" + opinion + "</opinion>\r\n";
					String fuente = eElement.getElementsByTagName("fuente").item(0).getTextContent();
					xml += "<fuente>" + fuente + "</fuente>\r\n";
				}
				xml += "</elem>\r\n";
			}
		}
		coleccionNoticias = new ArrayList<Noticia>();
		return xml;
	}
}
