/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bongo
 */
public class BorrarDuplicados {
	
	static void borrar(Configuracion config) throws IOException{		
		File folder = new File(config.getDirTrabajo() + "pruebas");				
		File[] listOfMediosPrensa = folder.listFiles();
		for (File medioPrensa : listOfMediosPrensa) {
			Map<String,String> tablaArchivos = new HashMap();
			File[] listOfFolders = medioPrensa.listFiles();
			for (File carpeta_fecha : listOfFolders) {
				File[] listOfFiles = carpeta_fecha.listFiles();
				for (File file1 : listOfFiles) {
					String nomb_arch = file1.getName();
					if (!tablaArchivos.containsKey(nomb_arch)) {
						tablaArchivos.put(nomb_arch, carpeta_fecha.getName());
					} else {
						String fecha_string1 = tablaArchivos.get(nomb_arch);
						String fecha_string2 = carpeta_fecha.getName();
						if (fecha_string1.compareTo(fecha_string2) < 0) {
							File archivoBorrar = new File (medioPrensa.getPath() + "\\" + fecha_string1 + "\\" + nomb_arch);
							archivoBorrar.delete();
							tablaArchivos.put(nomb_arch, fecha_string2);
						} else if (fecha_string1.compareTo(fecha_string2) > 0) {
							File archivoBorrar = new File (medioPrensa.getPath() + "\\" + fecha_string2 + "\\" + nomb_arch);
							archivoBorrar.delete();
						} else {
							throw new IOException("son el mismo archivo, aca hay algo mal");
						}
					}
				}
			}
		}
	}

}
