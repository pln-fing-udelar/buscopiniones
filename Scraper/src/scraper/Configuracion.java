/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodrigo
 */
public class Configuracion {

	private static String archConfig = "src\\scraper\\config.xml";
	private String dirOpiniones;
	private String dirFreeling;
	private String dirCorreferencias;
	private String dirPython;
	private String dirProlog;
	private String dirTrabajo;

	public Configuracion() throws ParserConfigurationException, SAXException, IOException {
		System.out.println("toy aca 1");
		File fXmlFile = new File(archConfig);
		System.out.println("toy aca 2");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		dirOpiniones = doc.getDocumentElement().getElementsByTagName("dirOpiniones").item(0).getTextContent();
		dirFreeling = doc.getDocumentElement().getElementsByTagName("dirFreeling").item(0).getTextContent();
		dirCorreferencias = doc.getDocumentElement().getElementsByTagName("dirCorreferencias").item(0).getTextContent();
		dirPython = doc.getDocumentElement().getElementsByTagName("dirPython").item(0).getTextContent();
		dirProlog = doc.getDocumentElement().getElementsByTagName("dirProlog").item(0).getTextContent();
		dirTrabajo = doc.getDocumentElement().getElementsByTagName("dirTrabajo").item(0).getTextContent();
	}

	/**
	 * @return the dirOpiniones
	 */
	public String getDirOpiniones() {
		return dirOpiniones;
	}

	/**
	 * @return the dirFreeling
	 */
	public String getDirFreeling() {
		return dirFreeling;
	}

	/**
	 * @return the dirCorreferencias
	 */
	public String getDirCorreferencias() {
		return dirCorreferencias;
	}

	/**
	 * @return the dirPython
	 */
	public String getDirPython() {
		return dirPython;
	}

	/**
	 * @return the dirProlog
	 */
	public String getDirProlog() {
		return dirProlog;
	}

	/**
	 * @return the dirTrabajo
	 */
	public String getDirTrabajo() {
		return dirTrabajo;
	}
}
