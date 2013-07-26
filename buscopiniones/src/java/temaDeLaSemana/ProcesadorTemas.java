package temaDeLaSemana;

import buscopiniones.Noticia;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	public static String transformarAFechaSolr(String fecha) {
		Pattern p = Pattern.compile("(?i)([0-3][0-9])/([0-1][0-9])/([0-9][0-9][0-1][0-9])");
		Matcher m = p.matcher(fecha);

		if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(1)) <= 31)) {
			fecha = m.group(3) + "-" + m.group(2) + "-" + m.group(1) + "T00:00:00Z";
		}
		return fecha;
	}

	public Noticia getNoticiaDeLaSemana(String fechaInicial, String fechaFinal) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String fechaIni = transformarAFechaSolr(fechaInicial);
		String fechaFin = transformarAFechaSolr(fechaFinal);
		Map<String, Double> temas = getTemasDeLaSemana(fechaIni, fechaFin);
		Set<Map.Entry<String, Double>> temasEntrySet = temas.entrySet();
		if (temasEntrySet.isEmpty()) {
			System.out.println("Esta todo mal!!!!");
		}
		String busqueda = "title:(";
		int cant = 0;
		for (Map.Entry<String, Double> tema : temasEntrySet) {
			busqueda += tema.getKey() + "^" + tema.getValue() + " ";
			if (cant > 7){
				break;
			}
			cant++;
		}
		busqueda += ")";

		busqueda = URLEncoder.encode(busqueda, "UTF-8");
		String paramStart = "0";
		String paramRows = "1";
		String paramFecha = "fecha:[" + fechaIni + " TO " + fechaFin + "]";
		String url = urlSolrSelect + "?q=" + busqueda + "&fq=" + paramFecha + "&wt=xml&start=" + paramStart + "&rows=" + paramRows;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();
		
		System.out.println(url);

		Noticia noti = new Noticia();
		
		Node nodoResult = doc.getDocumentElement().getElementsByTagName("result").item(0);
		Element elementoResult = (Element) nodoResult;
		NodeList listaDocs = elementoResult.getElementsByTagName("doc");
		for (int i = 0; i < listaDocs.getLength(); i++) {
			Node nodoDoc = listaDocs.item(i);
			if (nodoDoc.getNodeType() == Node.ELEMENT_NODE) {
				Element elementoDoc = (Element) nodoDoc;
				NodeList listaElems = elementoDoc.getChildNodes();
				
				
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
						}
					}
				}
				
				
			}
		}
		return noti;
	}
}
