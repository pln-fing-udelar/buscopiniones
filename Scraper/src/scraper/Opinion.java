package scraper;

/**
 *
 * @author Rodrigo
 */
public class Opinion {

    private Noticia noticia;
    private String fuente;
    private String opinion;
    private String id;

    public Opinion(Noticia noti, String fuente, String opinion, String id) {
        this.noticia = noti;
        this.fuente = fuente;
        this.opinion = opinion;
        this.id = id;
    }

    public String toXML() {
        String xml = "<doc>\r\n";
        xml += noticia.toXML();
        xml += "<field name=\"fuente\">" + ProcesadorHTML.html2text(fuente) + "</field>\r\n";
        xml += "<field name=\"opinion\">" + ProcesadorHTML.html2text(opinion) + "</field>\r\n";
        xml += "<field name=\"id\">" + ProcesadorHTML.html2text(noticia.getUrl()) + "/" + id + "</field>\r\n";
        xml += "</doc>\r\n";
        return xml;
    }

    int countWords(String in) {
        String trim = in.trim();
        if (trim.isEmpty()) {
            return 0;
        }
        return trim.split("\\s+").length; //separate string around spaces
    }

    public boolean esDescartable() {
        if ((countWords(fuente) < 1)
                || fuente.trim().equals("se")
                || fuente.trim().equals("yo")
                || fuente.trim().equals("Yo")
                || fuente.trim().equals("Se")) {
            return true;
        }
        return false;
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

}
