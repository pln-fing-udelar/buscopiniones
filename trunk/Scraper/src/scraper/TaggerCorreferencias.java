/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Rodrigo
 */
public class TaggerCorreferencias {

	private String archCorreferencias;
	private String archPython;

	public TaggerCorreferencias(String archCorreferencias, String archPython) {
		this.archCorreferencias = archCorreferencias;
		this.archPython = archPython;

//		PythonInterpreter interpreter = new PythonInterpreter();
//		interpreter.exec("import sys\nsys.path.append('C:\\Fing\\ProyGrado\\Correferencias\\ModuloCorref\\Proyecto_v6.2')\nexecfile('C:\\Fing\\ProyGrado\\Correferencias\\ModuloCorref\\Proyecto_v6.2\\correferencias.py')");
//		// execute a function that takes a string and returns a string
//		PyObject someFunc = interpreter.get("funcName");
//		PyObject result = someFunc.__call__(new PyString("Test!"));
//		String realResult = (String) result.__tojava__(String.class);
	}

	public void taggearCorreferencias() throws IOException {
		System.out.println(archPython + "python.exe" + " correferencias.py");
		ProcessBuilder builder = new ProcessBuilder(archPython + "python.exe", "correferencias.py");
		builder.directory(new File(archCorreferencias));
		builder.redirectErrorStream(true);
		Process process = builder.start();
		int returnCode = -1;
		try {
			returnCode = process.waitFor();
		} catch (InterruptedException ex) {
			System.out.println(ex);
		}
	}
}
