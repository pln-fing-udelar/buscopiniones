/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscopiniones;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodrigo
 */
public class BuscadorOpiniones {

	private static String urlSolrSelect = "http://127.0.0.1:8983/solr/collection1/select";

	public static String html2text(String html) {
		if (html == null || html.equals("")) {
			return "";
		}

		String textoOk = Jsoup.parse(html).text();
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		TagNode node = cleaner.clean(textoOk);
		TagNode[] nodos = node.getElementsByName("body", true);
		String ret = cleaner.getInnerHtml(nodos[0]);
		if (ret == null) {
			ret = "";
		}
		return ret;
	}

	public Collection<Opinion> getOpiniones(String fuente, String asunto, String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String paramFecha = "";
		if (fechaIni != null && !fechaIni.equals("") && fechaFin != null && !fechaFin.equals("")) {
			paramFecha = "fecha:[" + fechaIni + " TO " + fechaFin + "]";
		}

		paramFecha = URLEncoder.encode(paramFecha, "UTF-8");
		String paramStart = "0";
		String paramQ = "fuente:(" + fuente + ") AND (" + asunto + ")";
		paramQ = URLEncoder.encode(paramQ, "UTF-8");
		String paramRows = "100";
		String url = urlSolrSelect + "?q=" + paramQ + "&fq=" + paramFecha + "&wt=xml&start=" + paramStart + "&rows=" + paramRows;
		System.out.println(url);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();

		Collection<Opinion> opiniones = new ArrayList<Opinion>();

		Node nodoResult = doc.getDocumentElement().getElementsByTagName("result").item(0);
		Element elementoResult = (Element) nodoResult;
		NodeList listaDocs = elementoResult.getElementsByTagName("doc");
		for (int i = 0; i < listaDocs.getLength(); i++) {
			Node nodoDoc = listaDocs.item(i);
			if (nodoDoc.getNodeType() == Node.ELEMENT_NODE) {
				Element elementoDoc = (Element) nodoDoc;
				NodeList listaElems = elementoDoc.getChildNodes();
				Noticia noti = new Noticia();
				Opinion opinion = new Opinion();
				for (int j = 0; j < listaElems.getLength(); j++) {
					Node nodoElem = listaElems.item(j);
					if (nodoElem.getNodeType() == Node.ELEMENT_NODE) {
						Element elementoRes = (Element) nodoElem;
						if (elementoRes.getAttribute("name").equals("url")) {
							noti.setUrl(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("articulo")) {
							noti.setArticulo(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("title")) {
							noti.setTitle(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("metatitle")) {
							noti.setMetatitle(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("h1")) {
							noti.setH1(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("fecha")) {
							noti.setFecha(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("categorias")) {
							noti.setCategorias(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("descripcion")) {
							noti.setDescripcion(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("autor")) {
							noti.setAutor(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("fuente")) {
							opinion.setFuente(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("opinion")) {
							opinion.setOpinion(elementoRes.getTextContent());
						} else if (elementoRes.getAttribute("name").equals("id")) {
							opinion.setId(elementoRes.getTextContent());
						}
					}
				}
				opinion.setNoticia(noti);
				opiniones.add(opinion);
			}
		}

		return opiniones;
	}

	public String getJSONOpiniones(String fuente, String asunto, String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String json = "{\n"
				+ "\"timeline\":\n"
				+ "{\n"
				+ "\"headline\":\"Lo que dijo " + fuente + " sobre " + asunto + "\",\n"
				+ " \"type\":\"default\",\n"
				+ " \"text\":\"People say stuff\",\n"
				+ " \"startDate\":\"2002,1,26\",\n"
				+ " \"date\": [";
		if (fuente == null || asunto == null) {
			//aca hay que hacer algo
		} else {
			Collection<Opinion> opiniones = this.getOpiniones(fuente, asunto, fechaIni, fechaFin);

			for (Opinion op : opiniones) {
				json += op.toJSON() + ",";
			}
		}
		json = json.substring(0, json.length() - 1);
		json += " ]\n"
				+ " }\n"
				+ " }";
		return json;
	}
}