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

/**
 *
 * @author Rodrigo
 */
public class TaggerOpiniones {

	private String archOpiniones;
	private String archFreeling;
	private String archProlog;

	public TaggerOpiniones(String archOpiniones, String archFreeling, String archProlog) {
		this.archOpiniones = archOpiniones;
		this.archFreeling = archFreeling;
		this.archProlog = archProlog;
	}

	public void taggearFreelingDesdeArchivo(String archInput, String archOutput) throws IOException {
		//String content = new Scanner(new File(archInput)).useDelimiter("\\Z").next();
		String content = Main.readFile(archInput, "utf8");
		taggearFreeling(content, archOutput);
	}

	public void taggearFreeling(String articulo, final String archOutput) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(archFreeling + "analyzer.exe", "-f" + archFreeling + "analyzer.cfg");
		builder.redirectErrorStream(true);
		Process process = builder.start();
		OutputStream stdin = process.getOutputStream();
		final InputStream stdout = process.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stdin)));

		new Thread(new Runnable() {

			public void run() {
				try {
					Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archOutput)));
					BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
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
		String content = Main.readFile(archOpiniones + "entrada", "Windows-1252");
		content = content.replaceAll("(?m) [0-9\\.]*$", "");
		Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archOpiniones + "entrada"), "Windows-1252"));
		bw.write(content);
		bw.close();

		// ejecuto el reconocedor de opiniones (controlEs.pl) usando la libreria jpl para prolog	

//		Query q2 =
//				new Query("inicio",
//				new Term[]{
//					new Atom(archOpiniones + "entrada"), // archivo de entrada
//					new Atom(archOpiniones + "salida"), // archivo de salida
//					new Atom(archOpiniones + "roEs.txt")}); // esto no se para que es
//
//		System.out.println(
//				"inicio "
//				+ (q2.query() ? "succeeded" : "failed"));
		
		System.out.println(archProlog + "swipl.exe" + " controlEs.pl");
		ProcessBuilder builder = new ProcessBuilder(archProlog + "swipl.exe", "controlEs.pl");
		builder.directory(new File(archOpiniones));
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		
		OutputStream stdin = process.getOutputStream();
		final InputStream stdout = process.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
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

		writer.print("inicio('entrada', 'salida', 'roEs.txt').\n");
		writer.close();
		
		
		int returnCode = -1;
		try {
			returnCode = process.waitFor();
		} catch (InterruptedException ex) {
			System.out.println(ex);
		}
	}
}
