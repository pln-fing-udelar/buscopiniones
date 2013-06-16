package scraper;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;

/**
 *
 * @author Rodrigo
 */
public class ProcesadorHTML {

	private String html;
	private String url;

	public ProcesadorHTML(String html, String url) {
		this.html = html;
		this.url = url;
	}

	public static String html2text(String html) {
		return Jsoup.parse(html).text();
	}

	public String obtenerCharset() {
		String charset = "";
		Pattern pattcharset = Pattern.compile("(?s)(?i)charset=\"?(.*?)(\"| )");
		Matcher mcharset = pattcharset.matcher(html);
		if (mcharset.find()) {
			charset = html2text(mcharset.group(1));
		}
		return charset.trim();
	}

	public String obtenerTitle() {
		String title = "";
		Pattern pattTitle = Pattern.compile("(?s)(?i)<title.*?>(.*?)</title>");
		Matcher mTitle = pattTitle.matcher(html);
		if (mTitle.find()) {
			title = html2text(mTitle.group(1));
		}
		return title;
	}

	public String obtenerMetaTitle() {
		String title = "";
		Pattern pattTitle = Pattern.compile("(?s)(?i)<meta.*?title.*?content=(\"|')(.*?)(\"|').*?>");
		Matcher mTitle = pattTitle.matcher(html);
		if (mTitle.find()) {
			title = html2text(mTitle.group(2));
		}
		return title;
	}

	public String obtenerH1() {
		String title = "";
		Pattern pattTitle = Pattern.compile("(?s)(?i)<h1.*?>(.*?)</h1>");
		Matcher mTitle = pattTitle.matcher(html);
		if (mTitle.find()) {
			title = html2text(mTitle.group(1));
		}
		return title;
	}

	public String procesar(String medioDePrensa) throws BoilerpipeProcessingException, XPathExpressionException, ParserConfigurationException {
		String xml = "<pagina>\r\n";

		xml += "<url>" + url + "</url>\r\n";

		String noticia = ArticleExtractor.INSTANCE.getText(html);
		xml += "<articulo>" + noticia + "</articulo>\r\n";

		String title = this.obtenerTitle();
		xml += "<title>" + title.trim() + "</title>\r\n";

		String metatitle = this.obtenerMetaTitle();
		xml += "<metatitle>" + metatitle.trim() + "</metatitle>\r\n";

		String h1 = this.obtenerH1();
		xml += "<h1>" + h1.trim() + "</h1>\r\n";

		String fecha = this.parseFechaPublicacion();
		xml += "<fecha>" + fecha + "</fecha>\r\n";

		String categoria = this.parseCategorias(medioDePrensa);
		xml += "<categorias>" + categoria + "</categorias>\r\n"; 
				
		xml += "</pagina>\r\n";
		return xml;
	}

	public String parseFechaPublicacion() throws XPathExpressionException, ParserConfigurationException {
		// con xpath
	//	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	//	DocumentBuilder builder = factory.newDocumentBuilder();
	//	Document doc = builder.parse(html);
		TagNode tagNode = new HtmlCleaner().clean(html);
		org.w3c.dom.Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//span[@class='tiempo_transcurrido']");

		Pattern p = Pattern.compile("(?i)((20)?[0-1][0-9]).?([0-1][0-9]).?([0-3][0-9])");
		Pattern p1 = Pattern.compile("(?i)(20[0-1][0-9]).?([0-1][0-9]).?([0-3][0-9])");
		Pattern p2 = Pattern.compile("(?i)([0-3]?[0-9]).de.(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)(.de|,| ,).?(20[0-1][0-9])");
		Pattern p3 = Pattern.compile("(?i)([0-3][0-9]).?([0-1]?[0-9]).?(20[0-1][0-9])");
		Pattern p4 = Pattern.compile("(?i)([0-3][0-9]).?([0-1][0-9]).?([0-1][0-9])");


		Matcher m = p.matcher(url);

		if (m.find() && (Integer.parseInt(m.group(3)) <= 12) && (Integer.parseInt(m.group(4)) <= 31)) { // trato de matchear la fecha en la url con el patron p
			System.out.println("hola1");
			return m.group(1) + "-" + m.group(3) + "-" + m.group(4) + "T00:00:00Z";
		}

		//m = p2.matcher(html);
		String resultadoXPath = expr.evaluate(doc);
		m = p2.matcher(resultadoXPath);

		if (m.find()) { // trato de matchear la fecha en el contenido de la pagina con el patron p2
			String aux2 = "";
			String mes = "01";
			System.out.println(m.group(2));
			if (m.group(2).toLowerCase().equals("enero")) {
				mes = "01";
			} else if (m.group(2).toLowerCase().equals("febrero")) {
				mes = "02";
			} else if (m.group(2).toLowerCase().equals("marzo")) {
				mes = "03";
			} else if (m.group(2).toLowerCase().equals("abril")) {
				mes = "04";
			} else if (m.group(2).toLowerCase().equals("mayo")) {
				mes = "05";
			} else if (m.group(2).toLowerCase().equals("junio")) {
				mes = "06";
			} else if (m.group(2).toLowerCase().equals("julio")) {
				mes = "07";
			} else if (m.group(2).toLowerCase().equals("agosto")) {
				mes = "08";
			} else if (m.group(2).toLowerCase().equals("septiembre")) {
				mes = "09";
			} else if (m.group(2).toLowerCase().equals("octubre")) {
				mes = "10";
			} else if (m.group(2).toLowerCase().equals("noviembre")) {
				mes = "11";
			} else if (m.group(2).toLowerCase().equals("diciembre")) {
				mes = "12";
			}

			if ((Integer.parseInt(m.group(1)) < 10) && (m.group(1).length() == 1)) {
				aux2 = "0";
			}
			System.out.println(m.group(1));
			return m.group(4) + "-" + mes + "-" + aux2 + m.group(1) + "T00:00:00Z";
		}

		m = p1.matcher(html);

		if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(3)) <= 31)) { // trato de matchear la fecha en el contenido de la pagina con el patron p
			System.out.println("hola3");
			return m.group(1) + "-" + m.group(2) + "-" + m.group(3) + "T00:00:00Z";
		}

		m = p3.matcher(html);

		if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(1)) <= 31)) { // trato de matchear la fecha en el contenido de la pagina con el patron p3
			String aux = "";
			String aux2 = "";
			if ((Integer.parseInt(m.group(2)) < 10) && (m.group(2).length() == 1)) {
				aux = "0";
			}
			if ((Integer.parseInt(m.group(1)) < 10) && (m.group(1).length() == 1)) {
				aux2 = "0";
			}
			return m.group(3) + "-" + aux + m.group(2) + "-" + m.group(1) + "T00:00:00Z";
		}

		m = p4.matcher(html);

		if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(1)) <= 31)) { // trato de matchear la fecha en el contenido de la pagina con el patron p4
			return "20" + m.group(3) + "-" + m.group(2) + "-" + m.group(1) + "T00:00:00Z";
		}

		return "";
		// En caso de que no haya encontrado ninguna fecha, le pongo la fecha de hoy (el dia que se indexó)
//		Date d = new Date();
//		String mes = "";
//		int month = d.getMonth() + 1;
//		if (month < 10) {
//			mes += "0";
//		}
//		mes += month;
//		String dia = "";
//		int day = d.getDate();
//		if (day < 10) {
//			dia += "0";
//		}
//		dia += day;
//		return (d.getYear() + 1900) + "-" + mes + "-" + dia + "T00:00:00Z";
	}

	String parseAutor(){
		return null;
	}
	
	String parseCategorias(String medioDePrensa){

		return null;
	}
}
