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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import temaDeLaSemana.ProcesadorTemas;

/**
 *
 * @author Rodrigo
 */
public class BuscadorOpiniones {

	private static String urlSolrSelect = "http://127.0.0.1:8983/solr/collection1/select";
	private static String urlSolrSpell = "http://127.0.0.1:8983/solr/collection1/spell";

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

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

	public void getSpellCheckFuenteAsunto(String fuente, String asunto, StringBuilder spellCheckFuente, StringBuilder spellCheckAsunto) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		spellCheckAsunto.append(getSpellCheck(asunto));
		spellCheckFuente.append(getSpellCheck(fuente));
	}

	public String getSpellCheck(String asunto) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		if (asunto == null) {
			return "";
		}
		String paramStart = "0";
		String paramQ = asunto;
		paramQ = URLEncoder.encode(paramQ, "UTF-8");
		String paramRows = "0";

		String url = urlSolrSpell + "?q=" + paramQ + "&wt=xml&start=" + paramStart + "&rows=" + paramRows + "&spellcheck=true&spellcheck.count=1&spellcheck.collate=true&spellcheck.maxCollations=1&defType=edismax&mm=10<-100&stopwords=true&lowercaseOperators=true";
		System.out.println(url);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();

		if (doc.getDocumentElement().getElementsByTagName("lst").getLength() <= 1) {
			return "";
		}
		Node nodoSpellCheck = doc.getDocumentElement().getElementsByTagName("lst").item(1);
		Element elementoSpellCheck = (Element) nodoSpellCheck;

		Node nodoSuggestions = elementoSpellCheck.getElementsByTagName("lst").item(0);
		Element elementoSuggestions = (Element) nodoSuggestions;

		String correctlySpelled = elementoSuggestions.getElementsByTagName("bool").item(0).getTextContent();
		System.out.println("correctlySpelled:" + correctlySpelled);
		if (correctlySpelled.equals("false")) {

			NodeList listaLst = elementoSuggestions.getElementsByTagName("lst");
			for (int i = 0; i < listaLst.getLength(); i++) {
				Node nodoDoc = listaLst.item(i);
				if (nodoDoc.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoDoc = (Element) nodoDoc;
					if (elementoDoc.getAttribute("name").equals("collation")) {
						return elementoDoc.getElementsByTagName("str").item(0).getTextContent();
					}
				}
			}
		}

		return "";
	}

	public Collection<String> getFuentesRelacionadas(String fuente, String asunto, String fechaIni, String fechaFin) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		String paramFecha = "";
		if (fechaIni != null && !fechaIni.equals("") && fechaFin != null && !fechaFin.equals("") && !fechaIni.equals("null") && !fechaFin.equals("null")) {
			paramFecha = "fecha:[" + ProcesadorTemas.transformarAFechaSolr(fechaIni) + " TO " + ProcesadorTemas.transformarAFechaSolr(fechaFin) + "]";
		}
		if (asunto == null || asunto.equals("") || asunto.equals("null")) {
			return new ArrayList<String>();
		}
		paramFecha = URLEncoder.encode(paramFecha, "UTF-8");
		String paramStart = "0";
//		String paramQ = "(title:(" + asunto + ")^2 metatitle:(" + asunto + ")^2 h1:(" + asunto + ")^2"
//				+ " descripcion:(" + asunto + ")"
//				+ " opinion:(" + asunto + ")^10"
//				+ " articulo:(" + asunto + "))";
		String paramQ = asunto;
		paramQ = URLEncoder.encode(paramQ, "UTF-8");
		String paramRows = "0";
		String paramFacetLimit = "10";
		String paramFacetField = "fuente_facetado";

		String paramQf = "text title h1 descripcion opinion^10";
		paramQf = URLEncoder.encode(paramQf, "UTF-8");

		String url = urlSolrSelect + "?q=" + paramQ + "&fq=" + paramFecha + "&wt=xml&start=" + paramStart + "&rows=" + paramRows + "&facet=true&facet.field=" + paramFacetField + "&facet.limit=" + paramFacetLimit + "&group=true&group.field=opinion_sin_stemm&defType=edismax&mm=2<75%25+5<50%25&stopwords=true&lowercaseOperators=true&qf=" + paramQf;
		System.out.println(url);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();

		Collection<String> fuentes = new ArrayList<String>();

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression exprMatches = xpath.compile("//lst[@name='fuente_facetado']");
		NodeList listaMatches = (NodeList) exprMatches.evaluate(doc, XPathConstants.NODESET);
		Node nodoFuente_sin_stemm = listaMatches.item(0);
		Element elementoFuente_sin_stemm = (Element) nodoFuente_sin_stemm;

		NodeList listaFuentes = elementoFuente_sin_stemm.getElementsByTagName("int");
		for (int i = 0; i < listaFuentes.getLength(); i++) {
			Node nodoDoc = listaFuentes.item(i);
			if (nodoDoc.getNodeType() == Node.ELEMENT_NODE) {
				Element elementoDoc = (Element) nodoDoc;
				int cant = Integer.parseInt(elementoDoc.getTextContent());
				if (cant > 4 
						&& !elementoDoc.getAttribute("name").equalsIgnoreCase("fernando") 
						&& !elementoDoc.getAttribute("name").equalsIgnoreCase("país")
						&& !elementoDoc.getAttribute("name").equalsIgnoreCase("pais")) {
					fuentes.add(elementoDoc.getAttribute("name"));
				}

			}
		}
		return fuentes;
	}

	public String getTextoParaMostrar(String idOpinion, String opinion) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String paramIdOpinion = "id:(" + idOpinion.replace(":", "\\:") + ")";
		paramIdOpinion = URLEncoder.encode(paramIdOpinion, "UTF-8");
		String paramStart = "0";
		String paramQ = "articulo:(" + opinion.replaceAll("\"", "").replaceAll(":", "").replaceAll("AND", " ") + ")";
		paramQ = URLEncoder.encode(paramQ, "UTF-8");
		String paramRows = "1";
		String fragsize = "5000";
		String fl = "articulo";
		String url = urlSolrSelect + "?q=" + paramQ + "&fq=" + paramIdOpinion + "&wt=xml&start=" + paramStart + "&rows=" + paramRows + "&hl=true&hl.fragsize=" + fragsize + "&hl.fl=" + fl;
		System.out.println(url);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();
		String ret = "";
		if (doc.getDocumentElement().getElementsByTagName("arr") != null && doc.getDocumentElement().getElementsByTagName("arr").getLength() > 1) {
			Node nodoResult = doc.getDocumentElement().getElementsByTagName("arr").item(1);
			Element elementoResult = (Element) nodoResult;
			ret = elementoResult.getElementsByTagName("str").item(0).getTextContent();
		}
		return ret;
	}

	public ArrayList<Opinion> getOpiniones(String fuente, String asunto, String fechaIni, String fechaFin, String medioDePrensa, String cantResultados)
			throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {



		// Para el medio de prensa
		String paramMedioDePrensa = "";
		if (medioDePrensa != null && !medioDePrensa.equals("") && !medioDePrensa.equals("null")) {
			if (medioDePrensa.equals("elobservador")) {
				paramMedioDePrensa = "url:*elobservador.com.uy*";
			} else if (medioDePrensa.equals("elpais")) {
				paramMedioDePrensa = "url:*elpais.com.uy*";
			} else if (medioDePrensa.equals("larepublica")) {
				paramMedioDePrensa = "url:*republica.com.uy* url:*diariolarepublica.net*";
				// *diariolarepublica.net*
			}
			paramMedioDePrensa = URLEncoder.encode(paramMedioDePrensa, "UTF-8");
			paramMedioDePrensa = "&fq=" + paramMedioDePrensa;
		}

		// Para la fecha
		String paramFecha = "";
		if (fechaIni != null && !fechaIni.equals("") && fechaFin != null && !fechaFin.equals("") && !fechaIni.equals("null") && !fechaFin.equals("null")) {
			paramFecha = "fecha:[" + ProcesadorTemas.transformarAFechaSolr(fechaIni) + " TO " + ProcesadorTemas.transformarAFechaSolr(fechaFin) + "]";
			paramFecha = URLEncoder.encode(paramFecha, "UTF-8");
			paramFecha = "&fq=" + paramFecha;
		}


		String paramStart = "0";
		fuente = fuente.replaceAll("([^ ]+) ([^ ]+)", "$1 AND $2");
		String paramFuente = "";
		if (fuente != null && !fuente.equals("") && !fuente.equals("null")) {
			paramFuente = "fuente:(" + fuente + ")";
			paramFuente = URLEncoder.encode(paramFuente, "UTF-8");
			paramFuente = "&fq=" + paramFuente;
		}

		// fuente_corref:(" + fuente + ")^0.001)
//		String paramQ = "(title:(" + asunto + ")^2 metatitle:(" + asunto + ")^2 h1:(" + asunto + ")^2"
//				+ " descripcion:(" + asunto + ")"
//				+ " opinion:(" + asunto + ")^10"
//				+ " articulo:(" + asunto + "))";

		String paramQ = asunto;
		paramQ = URLEncoder.encode(paramQ, "UTF-8");
		if (asunto == null || asunto.isEmpty() || asunto.equals("null")){
			paramQ = "*:*";
		}
		
		// por que esta el campo text en el qf?
		String paramQf = "text title h1 descripcion opinion^6";
		paramQf = URLEncoder.encode(paramQf, "UTF-8");
		String paramPf = paramQf;
		String paramPs = "1";
		paramPs = URLEncoder.encode(paramPs, "UTF-8");
		
		// Maximo deberia ser 100
		String paramRows = "30";
		boolean validoCantResult = ((cantResultados != null && !cantResultados.equals("") && !cantResultados.equals("null")) && isInteger(cantResultados) && (Integer.parseInt(cantResultados) > 0));
		if (validoCantResult) {
			if (Integer.parseInt(cantResultados) > 100){
				cantResultados = "100";
			}
			paramRows = cantResultados;
		}

		
		/* Armamos la url para solr */
		String url = urlSolrSelect + "?q=" + paramQ + paramFecha + paramMedioDePrensa + paramFuente
				+ "&wt=xml&start=" + paramStart + "&rows=" + paramRows 
				+ "&group=true&group.field=opinion_sin_stemm&defType=edismax&mm=2<75%25+5<50%25&stopwords=true&lowercaseOperators=true&qf=" 
				+ paramQf + "&pf=" + paramPf + "&ps=" + paramPs; //+ "&group=true&group.field=opinion_sin_stemm"
		
		System.out.println(url);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		doc.getDocumentElement().normalize();

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		XPathExpression exprMatches = xpath.compile("//int[@name='matches']");
		NodeList listaMatches = (NodeList) exprMatches.evaluate(doc, XPathConstants.NODESET);
		String cantMatches = (String) listaMatches.item(0).getTextContent();
		Integer matches = Integer.parseInt(cantMatches);
		int cantResult = ((int) (Math.log(matches + 1) / Math.log(1.5)));
		System.out.println("cantMatches: " + cantMatches);
		System.out.println("cantResult: " + cantResult);
		int paramRowsInt = Math.min(matches, Integer.parseInt(paramRows));
		if (validoCantResult) {
			cantResult = Math.max(cantResult, paramRowsInt);
		}

		ArrayList<Opinion> opiniones = new ArrayList<Opinion>();

		Node nodoResult = doc.getDocumentElement().getElementsByTagName("result").item(0);
		Element elementoResult = (Element) nodoResult;

		XPathExpression exprDoc = xpath.compile("//doc");
		NodeList listaDocs = (NodeList) exprDoc.evaluate(doc, XPathConstants.NODESET);
		System.out.println("listaDocs: " + listaDocs.getLength());
		//NodeList listaDocs = elementoResult.getElementsByTagName("doc");
		for (int i = 0; i < Math.min(listaDocs.getLength(), cantResult); i++) {
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
				opinion.setTextoParaMostrar(getTextoParaMostrar(opinion.getId(), asunto + " " + fuente));
				opiniones.add(opinion);
			}
		}

		return opiniones;
	}

	public String getJSONOpiniones(String fuente, String asunto, String fechaIni, String fechaFin, String medioDePrensa, String cantResultados) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		String json;
		
		if ((fuente == null || fuente.isEmpty() || fuente.equals("null")) && (asunto == null || asunto.isEmpty() || asunto.equals("null"))) {
			json = "{\n"
					+ "\"timeline\":\n"
					+ "{\n"
					+ "\"headline\":\"Utilice la barra de búsqueda para encontrar opiniones\",\n"
					+ " \"type\":\"default\",\n"
					+ " \"text\":\"Powered by Buscopiniones\",\n"
					+ " \"startDate\":\"2013,10,26\",\n"
					+ " \"date\": [ ";
			json += "{";
			json += "\"startDate\":\"2013,10,26\",";
			json += "\"headline\":\"\",";
			json += "\"text\":\"\",";
			json += "\"asset\":{\n"
					+ "                    \"media\":\"img/large/4.jpg\",\n"
					+ "                    \"credit\":\"\",\n"
					+ "                    \"caption\":\"\"\n"
					+ "                }";
			json += "}";

		} else {
			Collection<Opinion> opiniones = this.getOpiniones(fuente, asunto, fechaIni, fechaFin, medioDePrensa, cantResultados);
			if (opiniones.isEmpty()) {				
				json = "{\n"
						+ "\"timeline\":\n"
						+ "{\n"
						+ "\"headline\":\"No se han encontrado resultados\",\n"
						+ " \"type\":\"default\",\n"
						+ " \"text\":\"Powered by Buscopiniones\",\n"
						+ " \"startDate\":\"2013,10,26\",\n"
						+ " \"date\": [ ";
				json += "{";
				json += "\"startDate\":\"2013,10,26\",";
				json += "\"headline\":\"\",";
				json += "\"text\":\"\",";
				json += "\"asset\":{\n"
						+ "                    \"media\":\"img/large/4.jpg\",\n"
						+ "                    \"credit\":\"\",\n"
						+ "                    \"caption\":\"\"\n"
						+ "                }";
				json += "}";	
			
			} else {
				json = "{\n"
						+ "\"timeline\":\n"
						+ "{\n";
						if (asunto == null || asunto.isEmpty() || asunto.equals("null")) {
				json += "\"headline\":\"Lo que dijo " + fuente + "\",\n";
						} else if (fuente == null || fuente.isEmpty() || fuente.equals("null")) {
				json += "\"headline\":\"Lo que se dijo sobre " + asunto + "\",\n";			
						} else {
				json += "\"headline\":\"Lo que dijo " + fuente + " sobre " + asunto + "\",\n";								
						}
				json += " \"type\":\"default\",\n"
						+ " \"text\":\"Powered by Buscopiniones\",\n"
						+ " \"startDate\":\"2013,10,26\",\n"
						+ " \"date\": [ ";


				for (Opinion op : opiniones) {
					json += op.toJSON() + ",";
				}
				json = json.substring(0, json.length() - 1);
			}
		}

		json += " ]\n"
				+ " }\n"
				+ " }";
		return json;
	}
}
