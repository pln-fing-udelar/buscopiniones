package scraper;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Rodrigo
 */
public class Main {

	static public void generarCVSEntrenamiento(String medioPrensa) throws IOException {
		Map<String, Boolean> tablaUrls = LectorCSV.run(medioPrensa);
		List<Ejemplo> ejemplos = new LinkedList();
		File folder = new File("C:\\Fing\\ProyGrado\\entrenar\\" + medioPrensa + "\\");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				System.out.println(file.getName());
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] decodedBytes = decoder.decodeBuffer(file.getName());
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
		Ejemplo.guardarCSV("C:\\Fing\\ProyGrado\\csv\\ejemplos" + medioPrensa + ".csv", ejemplos);
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

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {

			Configuracion config = new Configuracion();
			final int maxIterFreeling = 5;

			// Creo una lista de ejemplos vacia, para entrenar

			System.out.println("toy aca!!!!!!!");
			System.out.println("Entrenar clasificador?s/n");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String entrenarClasificador = in.readLine();
			if (entrenarClasificador.equals("s")) {
				System.out.println("csv el pais");
				Main.generarCVSEntrenamiento("elpais");
				System.out.println("csv el observador");
				Main.generarCVSEntrenamiento("elobservador");
				System.out.println("csv la republica");
				Main.generarCVSEntrenamiento("larepublica");
				Clasificador clasifelpais = new Clasificador("C:\\Fing\\ProyGrado\\csv\\ejemploselpais.csv");
				Clasificador clasifelobservador = new Clasificador("C:\\Fing\\ProyGrado\\csv\\ejemploselobservador.csv");
				Clasificador clasiflarepublica = new Clasificador("C:\\Fing\\ProyGrado\\csv\\ejemploslarepublica.csv");
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
			if (borrarDuplicados.equals("s")) {
				BorrarDuplicados.borrar();
				//return;
			}
			/**
			 * *******************************************************************
			 * Cambie la carpeta para una de pruebas con archivos bajados a mano
			 * *
			 * *******************************************************************
			 */
//			File status = new File("log.txt");
			File folder = new File("C:\\Fing\\ProyGrado\\pruebas");
			File[] listOfMediosPrensa = folder.listFiles();
			Arrays.sort(listOfMediosPrensa);
			File empiezoEnA = new File("C:\\Fing\\ProyGrado\\log.txt");
			String empiezoEn = "";
			if (empiezoEnA.exists()) {
				empiezoEn = readFile(empiezoEnA, "UTF-8");
			}
			int totMediosPrensa = listOfMediosPrensa.length;
			System.out.println(empiezoEn);
			String[] informacionParaParar = empiezoEn.split(System.getProperty("line.separator"));
			File archivoParar;
			if (empiezoEnA.exists()) {
				archivoParar = new File(informacionParaParar[0]);
				System.out.println(informacionParaParar[0]);
			} else {
				archivoParar = new File("");
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			//get current date time with Date()
			Date date = new Date();
			String timeStamp = dateFormat.format(date);

			int iterMediosPrensa = 0;

			while ((iterMediosPrensa < totMediosPrensa) && (listOfMediosPrensa[iterMediosPrensa].compareTo(archivoParar) <= 0)) {
				System.out.println(listOfMediosPrensa[iterMediosPrensa].compareTo(archivoParar));
				iterMediosPrensa++;
			}
			System.out.println(iterMediosPrensa);
			if ((iterMediosPrensa > 0) && (listOfMediosPrensa[iterMediosPrensa - 1].compareTo(archivoParar) < 0)) {
				iterMediosPrensa--;
			}
			
			while (iterMediosPrensa < totMediosPrensa) {
				File medioPrensa = listOfMediosPrensa[iterMediosPrensa];
				File[] listOfFolders = medioPrensa.listFiles();
				Arrays.sort(listOfFolders);
				String medioActual = medioPrensa.getName();
				String nomArchivo = "C:\\Fing\\ProyGrado\\htmlprocesado\\" + medioActual + timeStamp + ".xml";
				String nomArchivoNoti = "C:\\Fing\\ProyGrado\\htmlprocesado\\" + medioActual + timeStamp + "Noticias.xml";
				Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomArchivo)));
				Writer bwNoticias = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomArchivoNoti)));
				bw.append("<add>");
				bwNoticias.append("<add>");
				ProcesadorPaginas proc = new ProcesadorPaginas(config, medioActual);
				int i = 0;
				int iterCarpetaFecha = 0;
				int totCarpetasFecha = listOfFolders.length;
				while ((iterCarpetaFecha < totCarpetasFecha) && (listOfFolders[iterCarpetaFecha].compareTo(archivoParar) <= 0)) {
					iterCarpetaFecha++;
				}
				if ((iterCarpetaFecha > 0) && listOfFolders[iterCarpetaFecha - 1].compareTo(archivoParar) < 0) {
					iterCarpetaFecha--;
				}
				
				// Para el tema de la semana
				String xmlNoticia = "";
				
				while (iterCarpetaFecha < totCarpetasFecha) {
					System.out.println("jairo");
					File carpeta_fecha = listOfFolders[iterCarpetaFecha];
					File[] listOfFiles = carpeta_fecha.listFiles();
					Arrays.sort(listOfFiles);
					String ultimo = "";
					int iterArchivo = 0;
					int totArchivo = listOfFiles.length;
					while ((iterArchivo < totArchivo) && (listOfFiles[iterArchivo].compareTo(archivoParar) <= 0)) {
						iterArchivo++;
					}
					while (iterArchivo < totArchivo) {
						File file = listOfFiles[iterArchivo];
						if (file.isFile()) {
							ultimo = file.getName();
							System.out.println(ultimo);
							BASE64Decoder decoder = new BASE64Decoder();
							byte[] decodedBytes = decoder.decodeBuffer(file.getName());
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
							i = 0;
							Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Fing\\ProyGrado\\log.txt"), "UTF-8"));
							status.write(file.getPath());
							status.close();
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
						i = 0;
						Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Fing\\ProyGrado\\log.txt"), "UTF-8"));
						status.write(carpeta_fecha.getPath());
						status.close();
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
				}
				Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Fing\\ProyGrado\\log.txt"), "UTF-8"));
				status.write(medioPrensa.getPath() + System.getProperty("line.separator"));
				status.close();
				bw.append("</add>");
				bwNoticias.append("</add>");
				bw.flush();
				bwNoticias.flush();
				bw.close();
				bwNoticias.close();
				iterMediosPrensa++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
