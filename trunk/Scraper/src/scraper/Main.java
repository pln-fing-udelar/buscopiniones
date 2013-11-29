package scraper;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import sun.misc.BASE64Decoder;

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

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			String sistemaOperativo = System.getProperty("os.name");
			boolean isWin = sistemaOperativo.startsWith("Win");
//			System.out.println((new File("C:\\Fing\\hola")).getName());
	
			Configuracion config = new Configuracion(isWin);
			final int maxIterFreeling = 25;

			// Creo una lista de ejemplos vacia, para entrenar

			System.out.println("toy aca!!!!!!!");
			System.out.println("Entrenar clasificador?s/n");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String entrenarClasificador = in.readLine();
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
			if (borrarDuplicados.equals("s")) {
				BorrarDuplicados.borrar(config);
				//return;
			}
			/**
			 * *******************************************************************
			 * Cambie la carpeta para una de pruebas con archivos bajados a mano
			 * *
			 * *******************************************************************
			 */
//			File status = new File("log.txt");
		
			File folder = new File(config.getDirTrabajo() + "pruebas");
			
			File[] listOfMediosPrensa = folder.listFiles();
			
			Arrays.sort(listOfMediosPrensa);
		
			
			File empiezoEnA = new File(config.getDirTrabajo() + "log.xml");

			
			String empiezoEn = "";
			if(!empiezoEnA.exists()){
				Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirTrabajo() + "log.xml"), "UTF-8"));
				bw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n");
				bw.append("<log>\r\n");
				bw.append("<elobservador></elobservador>\r\n");
				bw.append("<elpais></elpais>\r\n");
				bw.append("<larepublica></larepublica>\r\n");
				bw.append("</log>");
				bw.flush();
				bw.close();
			}
//			if (empiezoEnA.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(empiezoEnA);
				doc.getDocumentElement().normalize();
				
				// para cambiar el valor
				
				
				
				// empiezoEn = readFile(empiezoEnA, "UTF-8");
//			}
			int totMediosPrensa = listOfMediosPrensa.length;	
			System.out.println(empiezoEn);
//			String[] informacionParaParar = empiezoEn.split(System.getProperty("line.separator"));
			File archivoParar;
//			if (empiezoEnA.exists()) {
//				archivoParar = new File(informacionParaParar[0]);
//				System.out.println(informacionParaParar[0]);
//			} else {
//				archivoParar = new File("");
//			}

			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			//get current date time with Date()
			Date date = new Date();
			String timeStamp = dateFormat.format(date);

			int iterMediosPrensa = 0;

//			while ((iterMediosPrensa < totMediosPrensa) && (listOfMediosPrensa[iterMediosPrensa].compareTo(archivoParar) <= 0)) {
//				System.out.println(listOfMediosPrensa[iterMediosPrensa].compareTo(archivoParar));
//				iterMediosPrensa++;
//			}
//			System.out.println(iterMediosPrensa);
//			if ((iterMediosPrensa > 0) && (listOfMediosPrensa[iterMediosPrensa - 1].compareTo(archivoParar) < 0)) {
//				iterMediosPrensa--;
//			}
			
			while (iterMediosPrensa < totMediosPrensa) {
				int contador = 0;
				File medioPrensa = listOfMediosPrensa[iterMediosPrensa];
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
				archivoParar = new File(doc.getDocumentElement().getElementsByTagName(medioPrensa.getName()).item(0).getTextContent());
				while ((iterCarpetaFecha < totCarpetasFecha) && (listOfFolders[iterCarpetaFecha].compareTo(archivoParar) <= 0)) {
					iterCarpetaFecha++;
				}
				if ((iterCarpetaFecha > 0) && listOfFolders[iterCarpetaFecha - 1].compareTo(archivoParar) < 0) {
					iterCarpetaFecha--;
				}
				String rutaArch = "";
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
						rutaArch = file.getPath();
						contador++;
						if (file.isFile() && file.length() > 0) {
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
							
							//guardo el valor adecuado
							doc.getDocumentElement().getElementsByTagName(medioActual).item(0).setTextContent(rutaArch);
							TransformerFactory transformerFactory = TransformerFactory.newInstance();
							Transformer transformer = transformerFactory.newTransformer();
							DOMSource source = new DOMSource(doc);
							StreamResult result = new StreamResult(new File(config.getDirTrabajo() + "log.xml"));
							transformer.transform(source, result);
							
//							Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirTrabajo() + "log.txt"), "UTF-8"));
//							status.write(file.getPath());
//							status.close();
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

						//guardo el valor adecuado
						doc.getDocumentElement().getElementsByTagName(medioActual).item(0).setTextContent(rutaArch);
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(new File(config.getDirTrabajo() + "log.xml"));
						transformer.transform(source, result);						
						
//						Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirTrabajo() + "log.txt"), "UTF-8"));
//						status.write(carpeta_fecha.getPath());
//						status.close();
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
					//guardo el valor adecuado	
					doc.getDocumentElement().getElementsByTagName(medioActual).item(0).setTextContent(rutaArch);
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File(config.getDirTrabajo() + "log.xml"));
					transformer.transform(source, result);
				}
				
				//guardo el valor adecuado

				
//				Writer status = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config.getDirTrabajo() + "log.txt"), "UTF-8"));
//				status.write(medioPrensa.getPath() + System.getProperty("line.separator"));
//				status.close();
				
				bw.append("</add>");
				bwNoticias.append("</add>");
				bw.flush();
				bwNoticias.flush();
				bw.close();
				bwNoticias.close();
				if (contador == 0) {
					(new File(nomArchivo)).delete();
					(new File(nomArchivoNoti)).delete();
				}
				iterMediosPrensa++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
