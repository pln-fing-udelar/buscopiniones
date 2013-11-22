/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

/**
 *
 * @author Bongo
 */
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LectorCSV {

	public static Map<String,Boolean> run(String medioPrensa, Configuracion config) throws FileNotFoundException, IOException {
		String csvFile = config.getDirTrabajo() + "csv" + File.separator + "resultados"+ medioPrensa +".csv";
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
