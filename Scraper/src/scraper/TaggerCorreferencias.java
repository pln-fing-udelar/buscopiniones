/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
public class TaggerCorreferencias {

	private Configuracion config;

	public TaggerCorreferencias(Configuracion config) {
		this.config = config;
	}

	// Utiliza el proyecto de correferencias para aplicar los tags correspondientes a las opiniones
	// La salida queda en el archivo salidaFinal.xml
	// Para entender mejor esto leer el informe de correferencias: Informe_v3.0.pdf
	public void taggearCorreferencias() throws IOException {

		//		PythonInterpreter interpreter = new PythonInterpreter();
		//		interpreter.exec("import sys\nsys.path.append('C:\\Fing\\ProyGrado\\Correferencias\\ModuloCorref\\Proyecto_v6.2')\nexecfile('C:\\Fing\\ProyGrado\\Correferencias\\ModuloCorref\\Proyecto_v6.2\\correferencias.py')");
		//		// execute a function that takes a string and returns a string
		//		PyObject someFunc = interpreter.get("funcName");
		//		PyObject result = someFunc.__call__(new PyString("Test!"));
		//		String realResult = (String) result.__tojava__(String.class);

		System.out.println(config.getDirPython() + "python.exe" + " correferencias.py");
		ProcessBuilder builder = new ProcessBuilder(config.getDirPython() + "python.exe", "correferencias.py");
		builder.directory(new File(config.getDirCorreferencias()));
		builder.redirectErrorStream(true);
		Process process = builder.start();
		int returnCode = -1;
		try {
			returnCode = process.waitFor();
		} catch (InterruptedException ex) {
			System.out.println(ex);
		}
	}

	// A partir del archivo salidaFinal.xml, genera una lista de las opiniones encontradas en el mismo
	public Collection<Opinion> obtenerOpiniones(Noticia noti) throws ParserConfigurationException, SAXException, IOException {


		File fXmlFile = new File(config.getDirCorreferencias() + "salidaFinal.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		Element docElem = doc.getDocumentElement();
		docElem.normalize();

		Collection<Opinion> opiniones = new ArrayList<Opinion>();
		Collection<Fuente> fuentesRec = new ArrayList<Fuente>();
		obtenerFuentesRec(docElem, fuentesRec);
		obtenerOpinionesRecursivo(docElem, noti, opiniones, fuentesRec);

		for (Opinion op : opiniones) {
			if (!op.getFuente().getIdRec().equals("0") && !op.getFuente().getIdRec().equals("")) {
				for (Fuente fuenteRec : fuentesRec) {
					if (fuenteRec.getId().equals(op.getFuente().getIdRec())) {
						op.getFuente().setFuente(fuenteRec.getFuente());
						break;
					}
				}
			}
		}

		NodeList listaLinks = doc.getElementsByTagName("link");
		for (int i = 0; i < listaLinks.getLength(); i++) {
			Node nNode = listaLinks.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elemento = (Element) nNode;
				String idF1 = elemento.getAttribute("idF1");
				String idF2 = elemento.getAttribute("idF2");
				Opinion op1 = null;
				Opinion op2 = null;
				for (Opinion op : opiniones) {
					if (op.getFuente().getId().equals(idF1)) {
						op1 = op;
					} else if (op.getFuente().getId().equals(idF2)) {
						op2 = op;
					}
				}
				if (op1 != null && op2 != null) {
					String fuente1 = op1.getFuente().getFuente();
					op1.getFuente().setFuente(fuente1 + ", " + op2.getFuente().getFuente());
					op2.getFuente().setFuente(fuente1 + ", " + op2.getFuente().getFuente());
				}
			}
		}

		return opiniones;
	}

	private void obtenerOpinionesRecursivo(Node nodo, Noticia noti, Collection<Opinion> opiniones, Collection<Fuente> fuentesRec) {
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			Element elemento = (Element) nodo;
			NodeList listaOpiniones = elemento.getElementsByTagName("opinion");
			for (int temp = 0; temp < listaOpiniones.getLength(); temp++) {
				Node nNode = listaOpiniones.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String opinion = eElement.getTextContent();
					obtenerFuentesRec(eElement, fuentesRec);
					Fuente fuente;
					if (eElement.getElementsByTagName("fuente").getLength() > 0) {
						Element elemFuente = (Element) eElement.getElementsByTagName("fuente").item(0);
						String fuenteContent = elemFuente.getTextContent();
						String idFuente = elemFuente.getAttribute("id");
						String idRec = elemFuente.getAttribute("idRec");
						fuente = new Fuente(fuenteContent, idFuente, idRec);
					} else {
						fuente = new Fuente("", "", "");
					}

					Opinion op = new Opinion(noti, fuente, opinion);
					opiniones.add(op);
					obtenerOpinionesRecursivo(eElement, noti, opiniones, fuentesRec);
				}
			}
		}
	}

	private void obtenerFuentesRec(Node nodo, Collection<Fuente> fuentesRec) {
		if (nodo.getNodeType() == Node.ELEMENT_NODE) {
			Element elemento = (Element) nodo;
			NodeList listaFuentesRec = elemento.getElementsByTagName("fuenteRec");
			for (int i = 0; i < listaFuentesRec.getLength(); i++) {
				Node nNode = listaFuentesRec.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String fuenteContent = eElement.getTextContent();
					String idFuente = eElement.getAttribute("id");
					Fuente fuente = new Fuente(fuenteContent, idFuente, "");
					fuentesRec.add(fuente);
				}
			}
		}
	}
}
