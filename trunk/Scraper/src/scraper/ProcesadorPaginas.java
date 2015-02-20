/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author Rodrigo
 */
public class ProcesadorPaginas {

    private Clasificador clasificador;
    private Configuracion config;
    private String medioDePrensa;
    private Collection<Noticia> coleccionNoticias;

    public ProcesadorPaginas(Configuracion config, String medioDePrensa) {
        this.config = config;
        this.medioDePrensa = medioDePrensa;
        clasificador = new Clasificador(config.getDirTrabajo() + "csv" + File.separator + "ejemplos" + medioDePrensa + ".csv");
        clasificador.crearModelo();
        this.coleccionNoticias = new ArrayList<Noticia>();
    }

    public String procesar(ProcesadorHTML proc) throws BoilerpipeProcessingException, IOException, ParserConfigurationException, SAXException, Exception {
        // saque para afuera el procesador html, para poder utilizarlo en otros casos
        System.out.println("Empiezo a procesar HTML");
        Ejemplo ej = new Ejemplo(proc, false);
        System.out.println(ej);
        if (clasificador.clasificar(ej)) {
            System.out.println("si si si");
            Noticia noti = proc.procesar(medioDePrensa);
            if (noti.getArticulo().trim().isEmpty()) {
                return "";
            }
            coleccionNoticias.add(noti);
            return noti.toXML();
        } else {
            return "";
        }
    }

    public String taggear() throws BoilerpipeProcessingException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException {
        if (coleccionNoticias.isEmpty()) {
            return "";
        }
        String xml = "";
        System.out.println("Empiezo a procesar taggeo");
        Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirTrabajo() + "entradaFreeling.txt"), "UTF-8"));
        for (Noticia noti : coleccionNoticias) {
            bw.write(noti.getArticulo());
            bw.write("\r\n");
            bw.write("--------------------------------------------------------------");
            bw.write("\r\n");
        }
        bw.close();

        TaggerOpiniones tagger = new TaggerOpiniones(config);
        System.out.println("Empiezo a taggear con freeling");
        tagger.taggearFreelingDesdeArchivo(config.getDirTrabajo() + "entradaFreeling.txt", config.getDirTrabajo() + "salidaFreeling.txt");

        String[] arrFreeling = null;
        int contador = 0;
        while (arrFreeling == null || ((arrFreeling.length - 1) != coleccionNoticias.size() && contador < 10)) {
            String salidaFreeling = Main.readFile(config.getDirTrabajo() + "salidaFreeling.txt", "UTF-8");
            arrFreeling = salidaFreeling.split("(?m)-------------------------------------------------------------- -------------------------------------------------------------- Fz 1");
            contador++;
        }
        System.out.println("Termine con freeling");
        if (contador < 10) {
            int i = 0;
            for (Noticia noti : coleccionNoticias) {
                //i = salidaFreeling.indexOf("--------------------------------------------------------------", i+1);
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirOpiniones() + "entrada"), "UTF-8"));

                bw.write(arrFreeling[i++]);
                bw.flush();
                bw.close();
                System.out.println("Empiezo a taggear opiniones");
                tagger.taggearOpiniones();

                CopyFiles.copyWithChannels(config.getDirOpiniones() + "salida.xml", config.getDirOpiniones() + "salida_limpia.xml", false);

                System.out.println("Empiezo a scrapear opiniones");
                Collection<Opinion> opiniones = tagger.obtenerOpiniones(noti);
                for (Opinion op : opiniones) {
                    if (!op.esDescartable()) {
                        System.out.println(op.getOpinion());
                        xml += op.toXML();
                    }
                }
            }
            System.out.println("Termine con el taggeo!");
        }
        coleccionNoticias = new ArrayList<Noticia>();
        return xml;
    }
}
