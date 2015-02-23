/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodrigo
 */
public class TaggerOpiniones {

    private String archOpiniones;
    private String archFreeling;
    private String archProlog;
    private String dirTrabajo;

    public TaggerOpiniones(Configuracion config) {
        this.archOpiniones = config.getDirOpiniones();
        this.archFreeling = config.getDirFreeling();
        this.archProlog = config.getDirProlog();
        this.dirTrabajo = config.getDirTrabajo();
    }

    public void taggearFreelingDesdeArchivo(String archInput, String archOutput) throws IOException {
        String content = Main.readFile(archInput, "UTF-8");
        taggearFreeling(content, archOutput);
    }

    public void taggearFreeling(String articulo, final String archOutput) throws IOException {
        String freelingBin;
        ProcessBuilder builder;
        if (System.getProperty("os.name").startsWith("Win")) {
            freelingBin = "analyzer.exe";
            builder = new ProcessBuilder(archFreeling + freelingBin, "-f" + archFreeling + "analyzer.cfg");
        } else {
            freelingBin = "analyze";
            builder = new ProcessBuilder(archFreeling + freelingBin, "-f" + dirTrabajo + "analyzer.cfg");
        }
        builder.redirectErrorStream(true);
        Process process = builder.start();
        OutputStream stdin = process.getOutputStream();
        final InputStream stdout = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdin, "UTF-8")));

        new Thread(new Runnable() {

            public void run() {
                try {
                    Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archOutput), "UTF-8"));
                    BufferedReader br = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
                    String line;
                    String articuloTaggeado = "";
                    while ((line = br.readLine()) != null) {
//						System.out.println(line);
                        articuloTaggeado += line + "\n";
                    }
                    bw.write(articuloTaggeado);
                    bw.close();
                } catch (java.io.IOException e) {
                    System.out.println(e);
                }
            }
        }).start();

        writer.print(articulo);
        writer.close();

        int returnCode = -1;
        try {
            returnCode = process.waitFor();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
//		System.out.println(returnCode);
    }

    public void taggearOpiniones() throws FileNotFoundException, IOException {

        // primero saco los numeritos que pone freeling al final de cada linea porque no se usan
        String content = Main.readFile(archOpiniones + "entrada", "UTF-8");
        content = content.replaceAll("(?m) [0-9\\.]*$", "");
        Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archOpiniones + "entrada"), "UTF-8"));
        bw.write(content);
        bw.close();

        String prologBin;
        if (System.getProperty("os.name").startsWith("Win")) {
            prologBin = "swipl.exe";
        } else {
            prologBin = "swipl";
        }
        System.out.println(archProlog + prologBin + " controlEs.pl");
        ProcessBuilder builder = new ProcessBuilder(archProlog + prologBin, "-f", "controlEs.pl");
        builder.directory(new File(archOpiniones));
        builder.redirectErrorStream(true);
        Process process = builder.start();

        OutputStream stdin = process.getOutputStream();
        final InputStream stdout = process.getInputStream();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdin)));

        new Thread(new Runnable() {

            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (java.io.IOException e) {
                    System.out.println(e);
                }
            }
        }).start();

        writer.print("inicio('entrada', 'salida.xml', 'roEs.txt').\n");
        writer.close();

        int returnCode = -1;
        try {
            returnCode = process.waitFor();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    private void arreglarXML() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException {
        CopyFiles.copyWithChannels(archOpiniones + "salida.xml", archOpiniones + "salida_limpia.xml", false);

        String opinionesXML = Main.readFile(archOpiniones + "salida_limpia.xml", "UTF-8");

        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        TagNode node = cleaner.clean(opinionesXML);

        new PrettyXmlSerializer(props).writeToFile(node, archOpiniones + "salida_limpia.xml", "UTF-8");
        opinionesXML = Main.readFile(archOpiniones + "salida_limpia.xml", "UTF-8");
        opinionesXML = opinionesXML.replaceAll("(?s)<body>", "");
        opinionesXML = opinionesXML.replaceAll("(?s)<head />", "");
        opinionesXML = opinionesXML.replaceAll("(?s)</body>.*$", "");
        opinionesXML = opinionesXML.replaceAll("(?s)<html>", "");
        opinionesXML = replaceLowerCase(opinionesXML, "=(\"\\[([a-z],)*([a-z])\\]\")");

        Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archOpiniones + "salida_limpia.xml"), "UTF-8"));
        bw.write(opinionesXML);
        bw.close();

    }

    public String replaceLowerCase(final String input, final String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group().toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public Collection<Opinion> obtenerOpiniones(Noticia noti) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {

        this.arreglarXML();

        File fXmlFile = new File(archOpiniones + "salida_limpia.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        Element docElem = doc.getDocumentElement();
        docElem.normalize();

        Collection<Opinion> opiniones = new ArrayList<Opinion>();

        obtenerOpinionesAux(docElem, noti, opiniones);

        return opiniones;
    }

    private void obtenerOpinionesAux(Node nodo, Noticia noti, Collection<Opinion> opiniones) throws XPathExpressionException {
        if (nodo.getNodeType() == Node.ELEMENT_NODE) {
            int idOp = 0;
            Element elemento = (Element) nodo;
            NodeList listaOpiniones = elemento.getElementsByTagName("opinion");
            for (int temp = 0; temp < listaOpiniones.getLength(); temp++) {
                Node nNode = listaOpiniones.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String opinion = eElement.getTextContent();
                    String fuente = "";
                    int i = 0;
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xPath.evaluate("fuente",
                            eElement, XPathConstants.NODESET);

//                    while (eElement.getElementsByTagName("fuente").getLength() > i) {
                    while (nodes.getLength() > i) {
                        Element elemFuente = (Element) nodes.item(i);
                        fuente += elemFuente.getTextContent() + " ";
                        i++;
                    }
                    Opinion op = new Opinion(noti, fuente, opinion, idOp + "");
                    idOp++;
                    opiniones.add(op);
                }
            }
        }
    }
}
