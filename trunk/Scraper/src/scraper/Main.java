package scraper;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Rodrigo
 */
public class Main {

    static public void generarCVSEntrenamiento(String medioPrensa, Configuracion config) throws IOException {
        Map<String, Boolean> tablaUrls = LectorCSV.run(medioPrensa, config);
        List<Ejemplo> ejemplos = new LinkedList();
        File folder = new File(config.getDirTrabajo() + "entrenar" + File.separator + medioPrensa + File.separator);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                byte[] decodedBytes = DatatypeConverter.parseBase64Binary(file.getName());
                String url = new String(decodedBytes);
                System.out.println(url);

                String html = readFile(file, "UTF-8");

                if (ProcesadorHTML.obtenerCharset(html).equals("iso-8859-1") || ProcesadorHTML.obtenerCharset(html).equals("ISO-8859-1")) {

                    html = readFile(file, "ISO-8859-1");

                } else if (ProcesadorHTML.obtenerCharset(html).equals("Windows-1252") || ProcesadorHTML.obtenerCharset(html).equals("windows-1252")) {
                    html = readFile(file, "Windows-1252");
                }
                ProcesadorHTML procHTML = new ProcesadorHTML(html, url);
                Ejemplo ej = new Ejemplo(procHTML, tablaUrls.get(file.getName()));
                ejemplos.add(ej);
            }
        }
        Ejemplo.guardarCSV(config.getDirTrabajo() + "csv" + File.separator + "ejemplos" + medioPrensa + ".csv", ejemplos);
    }

    static public String readFile(String file, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        while (!reader.ready());
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }

    static public String readFile(File file, String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        while (!reader.ready());
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }
    
    public static void mover_archivos(List<Path> archivos_a_mover, File medioPrensa) throws IOException {
        for (Path origen : archivos_a_mover) {
            Path destino = FileSystems.getDefault().getPath(medioPrensa.getParentFile().getParent(),
                    "/paginas_procesadas", medioPrensa.getName(),
                    origen.getParent().getFileName().toString());
            if (!destino.toFile().exists()) {
                destino.toFile().mkdirs();
            }
            Files.move(origen, destino.resolve(origen.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            String sistemaOperativo = System.getProperty("os.name");
            boolean isWin = sistemaOperativo.startsWith("Win");
            
            Configuracion config = new Configuracion(isWin);
            final int maxIterFreeling = 25;

//            TaggerOpiniones pruebaTag = new TaggerOpiniones(config);
//            Collection<Opinion> opis = pruebaTag.obtenerOpiniones(new Noticia("","","","","","","","",""));
//            for (Opinion opi : opis){
//                System.out.println(opi.toXML());
//            }
            
            // Creo una lista de ejemplos vacia, para entrenar
            System.out.println("toy aca!!!!!!!");
            System.out.println("Entrenar clasificador?s/n");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String entrenarClasificador = in.readLine();
            System.out.println(entrenarClasificador);
            if (entrenarClasificador.equals("s")) {
                System.out.println("csv el pais");
                Main.generarCVSEntrenamiento("elpais", config);
                System.out.println("csv el observador");
                Main.generarCVSEntrenamiento("elobservador", config);
                System.out.println("csv la republica");
                Main.generarCVSEntrenamiento("larepublica", config);
                Clasificador clasifelpais = new Clasificador(config.getDirTrabajo() + "csv" + File.separator + "ejemploselpais.csv");
                Clasificador clasifelobservador = new Clasificador(config.getDirTrabajo() + "csv" + File.separator + "ejemploselobservador.csv");
                Clasificador clasiflarepublica = new Clasificador(config.getDirTrabajo() + "csv" + File.separator + "ejemploslarepublica.csv");
                System.out.println("entrenando el pais");
                clasifelpais.crearModelo();
                System.out.println("entrenando el observador");
                clasifelobservador.crearModelo();
                System.out.println("entrenando la republica");
                clasiflarepublica.crearModelo();
            }

            System.out.println("Esto puede ser muy lento");
            System.out.println("Borrar duplicados?s/n");
            in = new BufferedReader(new InputStreamReader(System.in));
            String borrarDuplicados = in.readLine();
            if ((borrarDuplicados != null) && (borrarDuplicados.equals("s"))) {
                BorrarDuplicados.borrar(config);
                return;
            }

            File folder = new File(config.getDirTrabajo() + "PaginasDescargadas");

            File[] listOfMediosPrensa = folder.listFiles();

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            //get current date time with Date()
            Date date = new Date();
            String timeStamp = dateFormat.format(date);

            // esto es para no generar xml tan grandes
            final int cantidad_de_archivos_a_procesar = 5000;
            

            for (File medioPrensa : listOfMediosPrensa) {
                File[] listOfFolders = medioPrensa.listFiles();
                Arrays.sort(listOfFolders);
                String medioActual = medioPrensa.getName();
                String nomArchivo = config.getDirTrabajo() + "htmlprocesado" + File.separator + medioActual + timeStamp + ".xml";
                String nomArchivoNoti = config.getDirTrabajo() + "htmlprocesado" + File.separator + medioActual + timeStamp + "Noticias.xml";
                Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomArchivo), "UTF-8"));
                Writer bwNoticias = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomArchivoNoti), "UTF-8"));
                bw.append("<add>");
                bwNoticias.append("<add>");
                ProcesadorPaginas proc = new ProcesadorPaginas(config, medioActual);
                int i = 0;
                int iterCarpetaFecha = 0;
                int totCarpetasFecha = listOfFolders.length;
                ArrayList<Path> archivos_a_mover = new ArrayList<Path>();
                int cantidad_de_archivos_procesados = 0;
                // Para el tema de la semana
                String xmlNoticia = "";

                while (iterCarpetaFecha < totCarpetasFecha) {

                    File carpeta_fecha = listOfFolders[iterCarpetaFecha];
                    File[] listOfFiles = carpeta_fecha.listFiles();
                    Arrays.sort(listOfFiles);
                    String ultimo = "";
                    int iterArchivo = 0;
                    int totArchivo = listOfFiles.length;

                    while (iterArchivo < totArchivo) {
                        File file = listOfFiles[iterArchivo];
                        archivos_a_mover.add(file.toPath());
                        if (file.isFile() && file.length() > 0) {
                            ultimo = file.getName();
                            System.out.println(ultimo);
                            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(file.getName());
                            String url = new String(decodedBytes);
                            System.out.println(url);
                            String html = readFile(file, "UTF-8");

                            if (ProcesadorHTML.obtenerCharset(html).equals("iso-8859-1") || ProcesadorHTML.obtenerCharset(html).equals("ISO-8859-1")) {

                                html = readFile(file, "ISO-8859-1");

                            } else if (ProcesadorHTML.obtenerCharset(html).equals("Windows-1252") || ProcesadorHTML.obtenerCharset(html).equals("windows-1252")) {
                                html = readFile(file, "Windows-1252");
                            }

                            ProcesadorHTML procHTML = new ProcesadorHTML(html, url);

                            String xmlNoticiaTmp = proc.procesar(procHTML);

                            if (!xmlNoticiaTmp.isEmpty()) {
                                xmlNoticia += "<doc>\r\n";
                                xmlNoticia += xmlNoticiaTmp;
                                xmlNoticia += "</doc>\r\n";
                            }

                            i++;

                        }

                        if (i >= maxIterFreeling) {
                            String xml = proc.taggear();
                            bw.append(xml);
                            bw.flush();
                            bwNoticias.append(xmlNoticia);
                            bwNoticias.flush();
                            xmlNoticia = "";
                            cantidad_de_archivos_procesados += i;
                            i = 0;
                            if (cantidad_de_archivos_procesados >= cantidad_de_archivos_a_procesar) {
                                cantidad_de_archivos_procesados = 0;
                                bw.append("</add>");
                                bwNoticias.append("</add>");
                                bw.flush();
                                bwNoticias.flush();
                                bw.close();
                                bwNoticias.close();
                                iterArchivo = totArchivo;
                                iterCarpetaFecha = totCarpetasFecha + 1;
                                mover_archivos(archivos_a_mover, medioPrensa);
                            }
                        }
                        iterArchivo++;
                    }

                    if (i >= maxIterFreeling) {
                        String xml = proc.taggear();
                        bw.append(xml);
                        bw.flush();
                        bwNoticias.append(xmlNoticia);
                        bwNoticias.flush();
                        xmlNoticia = "";
                        cantidad_de_archivos_procesados += i;
                        i = 0;
                        if (cantidad_de_archivos_procesados >= cantidad_de_archivos_a_procesar) {
                            cantidad_de_archivos_procesados = 0;
                            bw.append("</add>");
                            bwNoticias.append("</add>");
                            bw.flush();
                            bwNoticias.flush();
                            bw.close();
                            bwNoticias.close();
                            iterCarpetaFecha = totCarpetasFecha;
                            mover_archivos(archivos_a_mover, medioPrensa);
                        }
                    }
                    iterCarpetaFecha++;
                }
                if (i != 0) {
                    String xml = proc.taggear();
                    bw.append(xml);
                    bw.flush();
                    bwNoticias.append(xmlNoticia);
                    bwNoticias.flush();
                    xmlNoticia = "";
                    cantidad_de_archivos_procesados += i;
                }
                if (cantidad_de_archivos_procesados > 0) {
                    Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirTrabajo() + "log.txt"), "UTF-8"));
                    status.write(medioPrensa.getPath() + System.getProperty("line.separator"));
                    status.close();
                    bw.append("</add>");
                    bwNoticias.append("</add>");
                    bw.flush();
                    bwNoticias.flush();
                    bw.close();
                    bwNoticias.close();
                    mover_archivos(archivos_a_mover, medioPrensa);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
