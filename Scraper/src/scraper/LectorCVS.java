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
import java.util.HashMap;
import java.util.Map;

public class LectorCVS {

	public static Map<String,Boolean> run() throws FileNotFoundException, IOException {

		String csvFile = "C:\\Fing\\ProyGrado\\cvs\\resultados.cvs";
		String coma = ",";
		Map<String,Boolean> retorno = new HashMap();
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = br.readLine();

		while ((line = br.readLine()) != null) {		
			String[] campos = line.split(coma);	
			
			retorno.put(campos[0], campos[1].equals("true"));	
		}
		
		return retorno;

	}
}
