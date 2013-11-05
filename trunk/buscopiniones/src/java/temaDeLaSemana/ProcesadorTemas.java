package temaDeLaSemana;

import buscopiniones.BuscadorOpiniones;
import buscopiniones.Noticia;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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

	public ArrayList<String> getUrlsNoticiasDeLaSemana(String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		String paramFecha = "fecha:[" + fechaIni + " TO " + fechaFin + "]";
		paramFecha = URLEncoder.encode(paramFecha, "UTF-8");
		String paramStart = "0";
//		String paramQ = "url2:elpais.com.uy";
		String paramQ = "*:*";
		paramQ = URLEncoder.encode(paramQ, "UTF-8");
		String paramRows = "1000";
		String url = urlSolrSelect + "?q=" + paramQ + "&fq=" + paramFecha + "&wt=xml&start=" + paramStart + "&rows=" + paramRows + "&group=true&group.field=articulo";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		ArrayList<String> urls = new ArrayList<String>();

		Node nodoResult = doc.getDocumentElement().getElementsByTagName("result").item(0);
		Element elementoResult = (Element) nodoResult;

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression exprDoc = xpath.compile("//doc");
		NodeList listaDocs = (NodeList) exprDoc.evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < listaDocs.getLength(); i++) {
			Node nodoDoc = listaDocs.item(i);
			if (nodoDoc.getNodeType() == Node.ELEMENT_NODE) {
				Element elementoDoc = (Element) nodoDoc;
				String urlElementoDoc = elementoDoc.getElementsByTagName("str").item(0).getTextContent();
				urls.add(urlElementoDoc);
			}
		}
		System.out.println("Cantidad de noticias encontradas en la semana: " + urls.size());
		return urls;
	}

	public ArrayList<Map<String, Double>> getTemasDeLaSemana(String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		Map<String, Double> temas = new HashMap<String, Double>();
		ArrayList<String> urls = getUrlsNoticiasDeLaSemana(fechaIni, fechaFin);
		Collection<NoticiaCluster> arrNoticias = new ArrayList();

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
			if (listaFloats.getLength() == 15) {
				SortedMap<String, Double> vectNoticia = new TreeMap<String, Double>();
				for (int i = 0; i < listaFloats.getLength(); i++) {

					Node nodoFloat = listaFloats.item(i);
					if (nodoFloat.getNodeType() == Node.ELEMENT_NODE) {
						Element elementoFloat = (Element) nodoFloat;
						Double boost = Double.parseDouble(elementoFloat.getTextContent());
						String nombreTema = elementoFloat.getAttribute("name").replace("mlt_articulo:", "");

						vectNoticia.put(nombreTema, boost);

						Double relevancia = 0.0;
						if (temas.containsKey(nombreTema)) {
							relevancia = temas.get(nombreTema);
						}
						relevancia += boost;
						temas.put(nombreTema, relevancia);
					}
				}

				arrNoticias.add(new NoticiaCluster(url, vectNoticia));

			}

			/*url = urlSolrMLTTitle + "?q=" + paramUrl + "&wt=xml&start=" + paramStart + "&rows=" + paramRows;

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
			 }*/

		}
		StringBuilder palCSV = new StringBuilder();

		for (String tema : temas.keySet()) {
			for (NoticiaCluster noti : arrNoticias) {
				if (!noti.getVectNoticia().containsKey(tema)) {
					noti.getVectNoticia().put(tema, 0.0);
				}
			}

		}
		for (String tema : arrNoticias.iterator().next().getVectNoticia().keySet()) {
			palCSV.append(tema.replaceAll(",", ".")).append(",");
		}
		palCSV.append("ult").append(System.getProperty("line.separator"));

		for (NoticiaCluster noti : arrNoticias) {
			//System.out.println(vect.keySet());
			for (Double valor : noti.getVectNoticia().values()) {
				palCSV.append(valor).append(",");
			}
			palCSV.append("ult");
			palCSV.append(System.getProperty("line.separator"));
		}
		Writer out = new OutputStreamWriter(new FileOutputStream("C:\\Fing\\ProyGrado\\temadelasemana.csv", false), Charset.forName("ISO-8859-15"));
		out.write(palCSV.toString());
		out.close();

		Clustering cluster = new Clustering("C:\\Fing\\ProyGrado\\temadelasemana.csv");
		cluster.crearModelo();
		SortedMap<Integer, Collection<NoticiaCluster>> noticiasClusterizadas = new TreeMap();
		int i = 0;
		for (NoticiaCluster noti : arrNoticias) {
			Integer numCluster = cluster.clasificar(noti.getVectNoticia().values());
			noti.setNumCluster(numCluster);
			Collection<NoticiaCluster> col = new ArrayList<NoticiaCluster>();
			if (noticiasClusterizadas.containsKey(numCluster)) {
				col = noticiasClusterizadas.get(numCluster);
			}
			col.add(noti);
			noticiasClusterizadas.put(numCluster, col);
		}
		System.out.println(noticiasClusterizadas);
		ArrayList<Map<String, Double>> temasClusterizados = new ArrayList();
		for (Collection<NoticiaCluster> col : noticiasClusterizadas.values()) {
			Map<String, Double> temasCluster = new HashMap();
			for (NoticiaCluster noti : col) {
				for (Map.Entry<String, Double> entry : noti.getVectNoticia().entrySet()) {
					Double relevancia = entry.getValue();
					if (temasCluster.containsKey(entry.getKey())) {
						relevancia += temasCluster.get(entry.getKey());
					}
					temasCluster.put(entry.getKey(), relevancia);
				}
			}
			ValueComparator bvc = new ValueComparator(temasCluster);
			TreeMap<String, Double> temas_ordenados = new TreeMap<String, Double>(bvc);
			temas_ordenados.putAll(temasCluster);
			temasClusterizados.add(temas_ordenados);
		}

		ValueComparator bvc = new ValueComparator(temas);
		TreeMap<String, Double> temas_ordenados = new TreeMap<String, Double>(bvc);

		temas_ordenados.putAll(temas);
		//return temas_ordenados;
		return temasClusterizados;
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

	public Collection<Noticia> getNoticiaDeLaSemana(String fechaInicial, String fechaFinal) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
		String fechaIni = transformarAFechaSolr(fechaInicial);
		String fechaFin = transformarAFechaSolr(fechaFinal);
		Collection<Map<String, Double>> temasCluster = getTemasDeLaSemana(fechaIni, fechaFin);
		Collection<Noticia> noticias = new ArrayList();
		for (Map<String, Double> temas : temasCluster) {
			Set<Map.Entry<String, Double>> temasEntrySet = temas.entrySet();
			if (temasEntrySet.isEmpty()) {
				System.out.println("Esta todo mal!!!!");
			}

			String asunto = "";
			int cant = 0;
			for (Map.Entry<String, Double> tema : temasEntrySet) {
				System.out.println(tema.getKey() + "^" + tema.getValue() + " ");
				asunto += tema.getKey() + "^" + tema.getValue() + " ";
				if (cant > 7) {
					break;
				}
				cant++;
			}
			String busqueda = "title:(" + asunto + ") descripcion:(" + asunto + ") " + asunto;


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
			BuscadorOpiniones buscOp = new BuscadorOpiniones();
			Collection<String> fuentesRel = buscOp.getFuentesRelacionadas("", asunto, fechaInicial, fechaFinal);
			noti.setFuentesRel(fuentesRel);

			noticias.add(noti);
		}

		System.out.println("toy aca amistiqui");
		File dir = new File("C:\\Fing\\ProyGrado\\tmpTemas");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String input = fechaFinal;
		String format = "dd/MM/yyyy";

		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date = df.parse(input);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int anio = cal.get(Calendar.YEAR);

		String filePath = "C:\\Fing\\ProyGrado\\tmpTemas\\" + anio + "_" + week + ".bin";
		FileOutputStream fileOut = new FileOutputStream(filePath);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(noticias);
		out.close();
		fileOut.close();
		System.out.println("toy aca amistiqui2");

		return noticias;
	}
}
