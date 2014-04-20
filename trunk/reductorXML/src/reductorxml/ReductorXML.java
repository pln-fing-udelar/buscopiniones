package reductorxml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author Rodrigo
 */
public class ReductorXML {

	private static int maxSizeArchivo = 500000000;
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		final File folder = new File("./");
		try {
			for (final File fileEntry : folder.listFiles()) {
				if (!fileEntry.isDirectory() && fileEntry.getName().contains(".xml")) {
					String nombreArchivo = fileEntry.getName();
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nombreArchivo), "UTF-8"));
					int numArchivo = 0;
					Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nombreArchivo.replace(".xml", "") + "_" + numArchivo + ".xml"), "UTF-8"));
					int lengthArch = 0;
					String line;
					while ((line = br.readLine()) != null) {
						bw.append(line).append("\n");
						lengthArch += line.length();
						if(lengthArch >= maxSizeArchivo && line.equals("</doc>")){
							bw.append("</add>");
							bw.close();
							numArchivo++;
							bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nombreArchivo.replace(".xml", "") + "_" + numArchivo + ".xml"), "UTF-8"));
							bw.append("<add>");
							lengthArch = 0;
							
						}
					}
					bw.close();
					br.close();
					fileEntry.delete();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
