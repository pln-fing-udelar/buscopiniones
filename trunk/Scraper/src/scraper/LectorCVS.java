/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

/**
 *
 * @author Bongo
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LectorCVS {

	public void run() throws FileNotFoundException, IOException {

		String csvFile = "C:\\Fing\\ProyGrado\\cvs\\resultados.cvs";
		String coma = ",";



		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();
		while ((line = br.readLine()) != null) {

			// use comma as separator
			String[] campos = line.split(coma);
			System.out.println(campos[0]+ coma +campos[1]);
		}



	}
}
