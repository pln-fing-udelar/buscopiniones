package scraper;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlcleaner.CleanerProperties;
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
        if (html == null) {
            html = "";
        }
        if (url == null) {
            url = "";
        }
        this.html = html;
        this.url = url;
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

    static public String obtenerCharset(String html) {
        String charset = "";
        Pattern pattcharset = Pattern.compile("(?s)(?i)charset=\"?(.*?)(\"| )");
        Matcher mcharset = pattcharset.matcher(html);
        if (mcharset.find()) {
            charset = html2text(mcharset.group(1));
        }
        return charset.trim();
    }

    public String obtenerTitle(String medioDePrensa) {
        String title = "";
        // acá va para hacer el de el pais al 17 09 2013
        if (medioDePrensa.equals("elpais")) {
            Pattern pattTitle = Pattern.compile("(?s)(?i)<title.*?>(.*?)</title>");
            Matcher mTitle = pattTitle.matcher(html);
            if (mTitle.find()) {
                title = html2text(mTitle.group(1));
            }
            return title;
        }
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

    public Noticia procesar(String medioDePrensa) throws BoilerpipeProcessingException {

        return new Noticia(url,
                ArticleExtractor.INSTANCE.getText(html).replaceAll("“|”", "\""),
                this.obtenerTitle(medioDePrensa).trim(),
                this.obtenerMetaTitle().trim(),
                this.obtenerH1().trim(),
                this.parseFechaPublicacion(medioDePrensa),
                this.parseCategorias(medioDePrensa),
                this.parseMetaDescripcion(medioDePrensa),
                this.parseAutor());
    }

    public String parseFechaPublicacion(String medioDePrensa) {

		//span[@class='tiempo_transcurrido']
        Pattern p = Pattern.compile("(?i)([0-1][0-9]).?([0-1][0-9]).?([0-3][0-9])");
        Pattern p1 = Pattern.compile("(?i)(20[0-1][0-9]).?([0-1][0-9]).?([0-3][0-9])");
        Pattern p2 = Pattern.compile("(?i)([0-3]?[0-9]).de.(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)(.de|,| ,).?(20[0-1][0-9])");
        Pattern p3 = Pattern.compile("(?i)([0-3][0-9]).?([0-1]?[0-9]).?(20[0-1][0-9])");
        Pattern p4 = Pattern.compile("(?i)([0-3][0-9]).?([0-1][0-9]).?([0-1][0-9])");
        Pattern p5 = Pattern.compile("(?i)([0-3][0-9])\\.([0-1][0-9])\\.(20[0-1][0-9])");
        Pattern p6 = Pattern.compile("(?i)Publicado el ([0-3]?[0-9])/([0-1]?[0-9])/(20[0-1][0-9])");
        if (medioDePrensa.equals("elobservador")) {
            Matcher m = p5.matcher(html);

            if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(1)) <= 31)) { // trato de matchear la fecha en el contenido de la pagina con el patron p4
                return m.group(3) + "-" + m.group(2) + "-" + m.group(1) + "T00:00:00Z";
            }
        } else if (medioDePrensa.equals("larepublica")) {
            Matcher m = p6.matcher(html);

            if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(1)) <= 31)) { // trato de matchear la fecha en el contenido de la pagina con el patron p4
                String aux1 = m.group(1);
                if (aux1.length() == 1) {
                    aux1 = "0" + aux1;
                }
                String aux2 = m.group(2);
                if (aux2.length() == 1) {
                    aux2 = "0" + aux2;
                }
                return m.group(3) + "-" + aux2 + "-" + aux1 + "T00:00:00Z";
            }
        } else if (medioDePrensa.equals("elpais")) {
            Pattern p7 = Pattern.compile("(?i)<span class=\"published\">.*?(ene|jan|feb|mar|abr|apr|may|jun|jul|ago|aug|sep|oct|nov|nob|dic|dec).([0-3]?[0-9]).(20[0-9][0-9]).*?</span>");
            Matcher m = p7.matcher(html);

            if (m.find()) {
                String aux2 = "";
                String mes = "01";
                if (m.group(1).toLowerCase().equals("ene") || m.group(1).toLowerCase().equals("jan")) {
                    mes = "01";
                } else if (m.group(1).toLowerCase().equals("feb")) {
                    mes = "02";
                } else if (m.group(1).toLowerCase().equals("mar")) {
                    mes = "03";
                } else if (m.group(1).toLowerCase().equals("abr") || m.group(1).toLowerCase().equals("apr")) {
                    mes = "04";
                } else if (m.group(1).toLowerCase().equals("may")) {
                    mes = "05";
                } else if (m.group(1).toLowerCase().equals("jun")) {
                    mes = "06";
                } else if (m.group(1).toLowerCase().equals("jul")) {
                    mes = "07";
                } else if (m.group(1).toLowerCase().equals("ago") || m.group(1).toLowerCase().equals("aug")) {
                    mes = "08";
                } else if (m.group(1).toLowerCase().equals("sep")) {
                    mes = "09";
                } else if (m.group(1).toLowerCase().equals("oct")) {
                    mes = "10";
                } else if (m.group(1).toLowerCase().equals("nov") || m.group(1).toLowerCase().equals("nob")) {
                    mes = "11";
                } else if (m.group(1).toLowerCase().equals("dic") || m.group(1).toLowerCase().equals("dec")) {
                    mes = "12";
                }
                if ((Integer.parseInt(m.group(2)) < 10) && (m.group(2).length() == 1)) {
                    aux2 = "0";
                }
                System.out.println(m.group(1));
                return m.group(3) + "-" + mes + "-" + aux2 + m.group(2) + "T00:00:00Z";
            }
        }
        Matcher m = p1.matcher(url);

        if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(3)) <= 31)) { // trato de matchear la fecha en la url con el patron p
            System.out.println("hola1");
            return m.group(1) + "-" + m.group(2) + "-" + m.group(3) + "T00:00:00Z";
        }

        m = p.matcher(url);

        if (m.find() && (Integer.parseInt(m.group(2)) <= 12) && (Integer.parseInt(m.group(3)) <= 31)) { // trato de matchear la fecha en la url con el patron p
            System.out.println("hola1");
            return "20" + m.group(1) + "-" + m.group(2) + "-" + m.group(3) + "T00:00:00Z";
        }

        m = p2.matcher(html);

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

        return "1990-08-14T00:00:00Z";
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

    String parseAutor() {
        String resultado = "";
        Pattern p = Pattern.compile("(?s)(<div class=\"author\">)(.*?)(</div>)");
        Matcher m = p.matcher(html);
        if (m.find()) {
            resultado = m.group(2);
        }
        return ProcesadorHTML.html2text(resultado);
    }

    String parseMetaDescripcion(String medioDePrensa) {
        String resultado = "";
        if (medioDePrensa.equals("elobservador")) {
            Pattern p = Pattern.compile("(?s)(?i)<meta.*?description.*?content=(\"|')(.*?)(\"|').*?>");
            Matcher m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(2);
            }
        } else if (medioDePrensa.equals("elpais")) {

            Pattern p = Pattern.compile("(?s)(?i)<meta.*?description.*?content=(\"|')(.*?)(\"|').*?>");
            Matcher m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(2);
            }
        } else if (medioDePrensa.equals("larepublica")) {
            Pattern p = Pattern.compile("(?s)(?i)<meta.*?description.*?content=(\"|')((\"|.)*?)(\"|')./>");
            Matcher m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(2);
            }
        }
        return ProcesadorHTML.html2text(resultado);
    }

    String parseCategorias(String medioDePrensa) {
        String resultado = "";
        if (medioDePrensa.equals("elobservador")) {
            Pattern p = Pattern.compile("(<div.class=\"story.*?\">)((.|\n|\r|\t)*?)(<h5>)((.|\n|\r|\t)*?)(</h5>)");
            Matcher m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(5);
                String[] aux = resultado.split("<b>|</b>");
                int n = aux.length;
                resultado = "";
                for (int i = 0; i < n; i++) {
                    resultado = resultado + aux[i];
                }
            }
        } else if (medioDePrensa.equals("elpais")) {
            // para agarrar la categoria de el pais al 24 09 2013
            Pattern p = Pattern.compile("(?s)(?i)<a href=\"/(.*?)\" class=\"logo\">");
            Matcher m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(1);
                return ProcesadorHTML.html2text(resultado);
            }

            // otro
            p = Pattern.compile("(?s)(?i)<h2.*?>(.*?)</h2>");
            m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(1);
                String[] aux = resultado.split("<b>|</b>");
                int n = aux.length;
                resultado = "";
                for (int i = 0; i < n; i++) {
                    resultado = resultado + aux[i];
                }
            }
        } else if (medioDePrensa.equals("larepublica")) {
			// de momento es imposible parsear la categoria para la republica...
            // para la republica 24 09 2013
            Pattern p = Pattern.compile("(?s)(?i)<p class=\"colgado\">(.*?)</p>");
            Matcher m = p.matcher(html);
            if (m.find()) {
                resultado = m.group(1);
                return ProcesadorHTML.html2text(resultado);
            }
        }
        return ProcesadorHTML.html2text(resultado);
    }

    String getUrl() {
        return url;
    }

    String getHtml() {
        return html;
    }

    int calcularTamanioHTML() {
        return html.length();
    }

    int calcularLargoUrl() {
        return url.length();
    }

    int cantidadTagsHTML() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)<(([a-zA-Z0-9].+?)|([ac-oq-zA-Z0-9].*?))>");
//		Pattern p = Pattern.compile("(?i)(<.*?>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    int calcularH1() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<h1>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    int calcularH2() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<h2>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    int calcularH3() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<h3>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    int calcularH4() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<h4>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    int calcularH5() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<h5>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    int calcularDiv() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<div>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    Integer calcularTable() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)(<table.*?>)");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

    // calcula los <p>
    Integer calcularParrafos() {
        int cantidad = 0;
        Pattern p = Pattern.compile("(?i)<p>");
        Matcher m = p.matcher(html);
        while (m.find()) {
            cantidad++;
        }
        return cantidad;
    }

}
