package buscopiniones;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Rodrigo
 */
public class Opinion {

	private Noticia noticia;
	private String fuente;
	private String opinion;
	private String id;
	private String textoParaMostrar;

	public Opinion() {
	}

	public Opinion(Noticia noti, String fuente, String opinion, String id) {
		this.noticia = noti;
		this.fuente = fuente;
		this.opinion = opinion;
		this.id = id;
		this.textoParaMostrar = "";
	}

	private String getFechaParaJSON() {
		String fecha = noticia.getFecha();
		Pattern p = Pattern.compile("(?i)([0-9][0-9][0-1][0-9])-([0-1][0-9])-([0-3][0-9])");
		Matcher m = p.matcher(fecha);

		if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(3)) <= 31)) {
			fecha = m.group(1) + "," + m.group(2) + "," + m.group(3);
		}
		return fecha;
	}

	public String getTextoOpinionOrig() {
		BreakIterator iterSentence = BreakIterator.getSentenceInstance();
		String source = this.getNoticia().getArticulo();
		iterSentence.setText(source);
		int start = iterSentence.first();
		ArrayList<String> arr = new ArrayList<String>();
		int i = 0;
		ArrayList<Integer> puntajeArr = new ArrayList<Integer>();
		for (int end = iterSentence.next(); end != BreakIterator.DONE; start = end, end = iterSentence.next()) {
			String sub = source.substring(start, end);
			arr.add(sub);
			BreakIterator iterWord = BreakIterator.getWordInstance();
			iterWord.setText(sub);
			int puntaje = 0;
			int startWord = iterWord.first();
			for (int endWord = iterWord.next(); endWord != BreakIterator.DONE; startWord = endWord, endWord = iterWord.next()) {
				String wordFraseOrig = sub.substring(startWord, endWord);
				BreakIterator iterOpinion = BreakIterator.getWordInstance();
				String op = this.getOpinion();
				iterOpinion.setText(op);
				int startOpinion = iterOpinion.first();
				for (int endOpinion = iterOpinion.next(); endOpinion != BreakIterator.DONE; startOpinion = endOpinion, endOpinion = iterOpinion.next()) {
					String wordOpinion = op.substring(startOpinion, endOpinion);
					if (wordOpinion.length() > 2 && wordOpinion.equals(wordFraseOrig)) {
						puntaje++;
						break;
					}
				}
			}
			puntajeArr.add(puntaje);
		}
		int maxInd = 0;
		int max = -1;
		int j = 0;
		for (int puntaje : puntajeArr) {
			if (puntaje > max) {
				maxInd = j;
				max = puntaje;
			}
			j++;
		}
		System.out.println(maxInd);
		return arr.get(maxInd);
	}

	public String toJSON() {
		String opinionJson = BuscadorOpiniones.html2text(this.getOpinion());

		opinionJson = opinionJson.replaceAll("(?i) de el ", " del ");
		opinionJson = opinionJson.replaceAll("(?i) a el ", " al ");
		opinionJson = opinionJson.replaceAll("&quot; (.*?) &quot;", "&quot;$1&quot;");
		String opinionJson2 = BuscadorOpiniones.html2text(this.getTextoOpinionOrig());
		if (opinionJson.length() + 10 <= opinionJson2.length()) {
			opinionJson = opinionJson2;
		}
		opinionJson = opinionJson.replaceAll(".*A\\+", "");
		opinionJson = opinionJson.replaceAll("^[A-ZÑÓÁÉÍÚ ][A-ZÑÓÁÉÍÚ ]+ ", "");
		opinionJson = opinionJson.replaceAll("^\\| [A-ZÑÓÁÉÍÚ ]* ", "");
		opinionJson = opinionJson.replaceAll(".*Publicado el [0-9 /:-]* ", "");
		opinionJson = opinionJson.replaceAll("¿ ?Te interesa esta .*", "");
		opinionJson = opinionJson.trim();


		Pattern p = Pattern.compile("(?i)<em>(.*?)</em>");
		Matcher m = p.matcher(this.getTextoParaMostrar());

		while (m.find()) {			
			opinionJson = opinionJson.replaceAll(m.group(1), "<b>" + m.group(1) + "</b>");

		}


		String tituloJson = BuscadorOpiniones.html2text(noticia.getTitle()).replaceAll("\\|.*$", "").replace("- Diario EL PAIS - Montevideo - Uruguay", "");
		String urlJson = BuscadorOpiniones.html2text(noticia.getUrl());
		String medio = "El País";
		if (noticia.getUrl().matches(".*republica\\.(com\\.uy|net).*")) {
			medio = "La República";
		} else if (noticia.getUrl().matches(".*observador\\.com\\.uy.*")) {
			medio = "El Observador";
		}
		BASE64Encoder encoder = new BASE64Encoder();
		String base64 = encoder.encode(noticia.getUrl().getBytes()).replaceAll("\r\n", "").replaceAll("\n", "");
		System.out.println(base64);
		String media = "ImagenNoticia/" + base64 + ".jpg";
//		String media = "http://localhost:8084/buscopiniones/ImagenNoticia/aHR0cDovL2hpc3Rvcmljby5lbHBhaXMuY29tLnV5LzEyMDYyNS91bHRtby02NDgyNjcvdWx0aW1vbW9tZW50by9NZWRpZGFzLXNvYnJlLWxhLW1hcmlodWFuYS1zb24tcGFyYS1wcm90ZWdlci1hbC1jb25zdW1pZG9yLWRpam8tTXVqaWNhLw==.jpg";
		String credit = "";
		if (opinionJson.matches(".*?&quot;.*?&quot;.*?")) {
			media = opinionJson.replaceAll(".*?&quot;(.*?)&quot;.*", "<blockquote>&quot;$1&quot;</blockquote>");
			credit = this.getFuente().replaceAll("A\\+", "");;
		}

		String json = "{";
		json += "\"startDate\":\"" + getFechaParaJSON() + "\",";
		json += "\"headline\":\"" + opinionJson + "\",";
		json += "\"text\":\"<p>" + tituloJson + "</p><p><a href ='" + urlJson + "'>" + medio + "</a></p>\",";
		json += "\"asset\":{\n"
				+ "                    \"media\":\"" + media + "\",\n"
				+ "                    \"credit\":\"" + credit + "\",\n"
				+ "                    \"caption\":\"\"\n"
				+ "                }";
		json += "}";

		return json;
	}

	public Date getDate() {
		Pattern p = Pattern.compile("(?i)([0-9][0-9][0-1][0-9])-([0-1][0-9])-([0-3][0-9])");
		Matcher m = p.matcher(this.noticia.getFecha());
		Calendar cal = Calendar.getInstance();
		if (m.find()) {
			cal.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
			cal.set(Calendar.MONTH, Integer.parseInt(m.group(2)));
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)));
		}
		Date dateRepresentation = cal.getTime();
		return dateRepresentation;
	}

	/**
	 * @return the noticia
	 */
	public Noticia getNoticia() {
		return noticia;
	}

	/**
	 * @return the fuente
	 */
	public String getFuente() {
		return fuente;
	}

	/**
	 * @return the opinion
	 */
	public String getOpinion() {
		return opinion;
	}

	/**
	 * @param noticia the noticia to set
	 */
	public void setNoticia(Noticia noticia) {
		this.noticia = noticia;
	}

	/**
	 * @param fuente the fuente to set
	 */
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}

	/**
	 * @param opinion the opinion to set
	 */
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the textoParaMostrar
	 */
	public String getTextoParaMostrar() {
		return textoParaMostrar;
	}

	/**
	 * @param textoParaMostrar the textoParaMostrar to set
	 */
	public void setTextoParaMostrar(String textoParaMostrar) {
		this.textoParaMostrar = textoParaMostrar;
	}
}
