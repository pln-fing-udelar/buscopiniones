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
import sun.misc.BASE64Decoder;

/**
 *
 * @author Rodrigo
 */
public class Main {

	static public String readFile(String file, String encoding) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		return stringBuilder.toString();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			TaggerOpiniones tagger = new TaggerOpiniones("C:\\Fing\\ProyGrado\\opiniones\\", "C:\\Fing\\ProyGrado\\Freeling\\freeling_win\\freeling\\bin\\");
			tagger.taggearFreelingDesdeArchivo("C:\\Fing\\ProyGrado\\Freeling\\freeling_win\\freeling\\bin\\prueba.txt", "C:\\Fing\\ProyGrado\\opiniones\\entrada");
			tagger.taggearOpiniones();

			CopyFiles.copyWithChannels("C:\\Fing\\ProyGrado\\opiniones\\salida", "C:\\Fing\\ProyGrado\\Correferencias\\ModuloCorref\\Proyecto_v6.2\\entrada.xml", false);
			TaggerCorreferencias tagger2 = new TaggerCorreferencias("C:\\Fing\\ProyGrado\\Correferencias\\ModuloCorref\\Proyecto_v6.2\\", "C:\\Python27\\");
			tagger2.taggearCorreferencias();
			return;

			File folder = new File("C:\\Fing\\ProyGrado\\paginas");
			File[] listOfMediosPrensa = folder.listFiles();
			for (File medioPrensa : listOfMediosPrensa) {
				File[] listOfFolders = medioPrensa.listFiles();
				String medioActual = medioPrensa.getName();
				String nomArchivo = "C:\\Fing\\ProyGrado\\htmlprocesado\\" + medioActual + ".xml";
				Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomArchivo)));
				for (File carpeta_fecha : listOfFolders) {
					File[] listOfFiles = carpeta_fecha.listFiles();
					for (File file : listOfFiles) {
						if (file.isFile()) {
							System.out.println(file.getName());
							BASE64Decoder decoder = new BASE64Decoder();
							byte[] decodedBytes = decoder.decodeBuffer(file.getName());
							String url = new String(decodedBytes);
							System.out.println(url);

							FileInputStream stream = new FileInputStream(file);
							FileChannel fc = stream.getChannel();
							MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
							String html = Charset.forName("UTF-8").decode(bb).toString();

							ProcesadorHTML proc = new ProcesadorHTML(html, url);
							if (proc.obtenerCharset().equals("iso-8859-1") || proc.obtenerCharset().equals("ISO-8859-1")) {
								stream = new FileInputStream(file);
								fc = stream.getChannel();
								bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
								html = Charset.forName("ISO-8859-1").decode(bb).toString();
								//						System.out.println(html + "ESTO ES EL FIN");
								proc = new ProcesadorHTML(html, url);
							}


							String xml = proc.procesar(medioActual);
							bw.append(xml);
							bw.flush();
						}
					}
				}
				bw.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
