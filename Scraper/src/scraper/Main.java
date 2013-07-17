package scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Rodrigo
 */
public class Main {

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

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {

			Configuracion config = new Configuracion();
			final int maxIterFreeling = 25;

			// Creo una lista de ejemplos vacia, para entrenar
			LinkedList<Ejemplo> ejemplos = new LinkedList();
			System.out.println("toy aca!!!!!!!");
			
			/*****************************************
			 * Cambie la carpeta para una de pruebas *
			 *****************************************/
			File folder = new File("C:\\Fing\\ProyGrado\\pruebas");
			
			File[] listOfMediosPrensa = folder.listFiles();
			for (File medioPrensa : listOfMediosPrensa) {
				File[] listOfFolders = medioPrensa.listFiles();
				String medioActual = medioPrensa.getName();
				String nomArchivo = "C:\\Fing\\ProyGrado\\htmlprocesado\\" + medioActual + ".xml";
				Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomArchivo)));
				bw.append("<add>");
				ProcesadorPaginas proc = new ProcesadorPaginas(config, medioActual);
				int i = 0;
				for (File carpeta_fecha : listOfFolders) {
					File[] listOfFiles = carpeta_fecha.listFiles();
					for (File file : listOfFiles) {
						if (file.isFile()) {
							System.out.println(file.getName());
							BASE64Decoder decoder = new BASE64Decoder();
							byte[] decodedBytes = decoder.decodeBuffer(file.getName());
							String url = new String(decodedBytes);
							System.out.println(url);

//							FileInputStream stream = new FileInputStream(file);
//							FileChannel fc = stream.getChannel();
//							MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//							String html = Charset.forName("UTF-8").decode(bb).toString();
							String html = readFile(file, "UTF-8");

							if (ProcesadorHTML.obtenerCharset(html).equals("iso-8859-1") || ProcesadorHTML.obtenerCharset(html).equals("ISO-8859-1")) {
//								stream = new FileInputStream(file);
//								fc = stream.getChannel();
//								bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//								html = Charset.forName("ISO-8859-1").decode(bb).toString();
								html = readFile(file, "ISO-8859-1");

							}else if (ProcesadorHTML.obtenerCharset(html).equals("Windows-1252") || ProcesadorHTML.obtenerCharset(html).equals("windows-1252")){
								html = readFile(file, "Windows-1252");
							}

							ProcesadorHTML procHTML = new ProcesadorHTML(html, url);
							
							// para crear los ejemplos de entrenamiento a mano
							BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
							System.out.println(url);
							System.out.println("Es articulo? s/n");
							String esArticuloStr = in.readLine();
							boolean esArticulo = esArticuloStr.equals("s");
							Ejemplo ej = new Ejemplo(procHTML, esArticulo);
							ejemplos.add(ej);
							Ejemplo.guardarCSV("C:\\Fing\\ProyGrado\\cvs\\ejemplos.cvs", ejemplos);
							proc.procesar(procHTML);
							
							i++;
						}
						if (i >= maxIterFreeling) {
							String xml = proc.taggear();
							bw.append(xml);
							bw.flush();
							i = 0;
						}
					}

					if (i >= maxIterFreeling) {
						String xml = proc.taggear();
						bw.append(xml);
						bw.flush();
						i = 0;
					}

				}
				if (i != 0) {
					String xml = proc.taggear();
					bw.append(xml);
					bw.flush();
				}
				bw.append("</add>");
				bw.flush();
				bw.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
