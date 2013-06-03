/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;
import jpl.*;

/**
 *
 * @author Rodrigo
 */
public class TaggerOpiniones {

	private String archOpiniones;
	private String archFreeling;

	public TaggerOpiniones(String archOpiniones, String archFreeling) {
		this.archOpiniones = archOpiniones;
		this.archFreeling = archFreeling;
	}

	public void taggearFreelingDesdeArchivo(String archInput, String archOutput) throws IOException {
		String content = new Scanner(new File(archInput)).useDelimiter("\\Z").next();
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
		System.out.println(articulo);


		new Thread(new Runnable() {

			public void run() {
				try {
					Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archOutput)));
					BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
					String line;
					String articuloTaggeado = "";
					while ((line = br.readLine()) != null) {
						System.out.println(line);
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
		System.out.println(returnCode);
	}

	public void taggearOpiniones() {
		Query q1 =
				new Query(
				"consult",
				new Term[]{new Atom(archOpiniones + "controlEs.pl")});
		System.out.println("consult " + (q1.query() ? "succeeded" : "failed"));

		Query q2 =
				new Query("inicio",
				new Term[]{
					new Atom(archOpiniones + "mujica"),
					new Atom(archOpiniones + "mujicasalida"),
					new Atom(archOpiniones + "roEs.txt")});

		System.out.println(
				"inicio "
				+ (q2.query() ? "succeeded" : "failed"));

	}
}
