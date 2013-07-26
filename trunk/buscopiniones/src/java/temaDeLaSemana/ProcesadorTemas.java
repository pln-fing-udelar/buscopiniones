package temaDeLaSemana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
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
public class ProcesadorTemas {

	private static String urlSolrSelect = "http://127.0.0.1:8984/solr/collection1/select";
	private static String urlSolrMLTArticulo = "http://127.0.0.1:8984/solr/collection1/mlt_articulo";
	private static String urlSolrMLTTitle = "http://127.0.0.1:8984/solr/collection1/mlt_title";

	public String getXML(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}

	public Collection<String> getUrlsNoticiasDeLaSemana(String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String paramFecha = "fecha:[" + fechaIni + " TO " + fechaFin + "]";
		paramFecha = URLEncoder.encode(paramFecha, "UTF-8");
		String paramStart = "0";
		String paramQ = "url2:elpais.com.uy";
		String paramRows = "1000";
		String url = urlSolrSelect + "?q=" + paramQ + "&fq=" + paramFecha + "&wt=xml&start=" + paramStart + "&rows=" + paramRows;
//		String xmlNoticiasDeLaSemana = getXML("http://127.0.0.1:8984/solr/collection1/select?q=*%3A*&fq=" + paramFecha + "&wt=xml&start=" + paramStart + "&rows=" + paramRows);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		Collection<String> urls = new ArrayList<String>();

		Node nodoResult = doc.getDocumentElement().getElementsByTagName("result").item(0);
		Element elementoResult = (Element) nodoResult;
		NodeList listaDocs = elementoResult.getElementsByTagName("doc");
		for (int i = 0; i < listaDocs.getLength(); i++) {
			Node nodoDoc = listaDocs.item(i);
			if (nodoDoc.getNodeType() == Node.ELEMENT_NODE) {
				Element elementoDoc = (Element) nodoDoc;
				String urlElementoDoc = elementoDoc.getElementsByTagName("str").item(0).getTextContent();
				urls.add(urlElementoDoc);
			}
		}

		return urls;
	}

	public Map<String, Double> getTemasDeLaSemana(String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		Map<String, Double> temas = new HashMap<String, Double>();

		Collection<String> urls = getUrlsNoticiasDeLaSemana(fechaIni, fechaFin);
		for (String urlNoticia : urls) {
			String paramUrl = "url2:" + urlNoticia.replace("http://", "");
			paramUrl = URLEncoder.encode(paramUrl, "UTF-8");
			String paramStart = "0";
			String paramRows = "0";
			String url = urlSolrMLTArticulo + "?q=" + paramUrl + "&wt=xml&start=" + paramStart + "&rows=" + paramRows;

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(url);
			doc.getDocumentElement().normalize();

			Node nodointerestingTerms = doc.getDocumentElement().getElementsByTagName("lst").item(1);
			Element elementointerestingTerms = (Element) nodointerestingTerms;
			System.out.println(url);
			NodeList listaFloats = elementointerestingTerms.getElementsByTagName("float");
			for (int i = 0; i < listaFloats.getLength(); i++) {

				Node nodoFloat = listaFloats.item(i);
				if (nodoFloat.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoFloat = (Element) nodoFloat;
					Double boost = Double.parseDouble(elementoFloat.getTextContent());
					String nombreTema = elementoFloat.getAttribute("name").replace("mlt_articulo:", "");
					Double relevancia = 0.0;
					if (temas.containsKey(nombreTema)) {
						relevancia = temas.get(nombreTema);
					}
					relevancia += boost;
					temas.put(nombreTema, relevancia);
				}
			}

			url = urlSolrMLTTitle + "?q=" + paramUrl + "&wt=xml&start=" + paramStart + "&rows=" + paramRows;

			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(url);
			doc.getDocumentElement().normalize();

			nodointerestingTerms = doc.getDocumentElement().getElementsByTagName("lst").item(1);
			elementointerestingTerms = (Element) nodointerestingTerms;
			System.out.println(url);
			listaFloats = elementointerestingTerms.getElementsByTagName("float");
			for (int i = 0; i < listaFloats.getLength(); i++) {

				Node nodoFloat = listaFloats.item(i);
				if (nodoFloat.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoFloat = (Element) nodoFloat;
					Double boost = Double.parseDouble(elementoFloat.getTextContent());
					String nombreTema = elementoFloat.getAttribute("name").replace("mlt_title:", "");
					Double relevancia = 0.0;
					if (temas.containsKey(nombreTema)) {
						relevancia = temas.get(nombreTema);
					}
					relevancia += boost;
					temas.put(nombreTema, relevancia);
				}
			}

		}
		ValueComparator bvc = new ValueComparator(temas);
		TreeMap<String, Double> temas_ordenados = new TreeMap<String, Double>(bvc);
		temas_ordenados.putAll(temas);
		return temas_ordenados;
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
