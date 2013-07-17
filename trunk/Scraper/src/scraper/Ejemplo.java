/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

/**
 *
 * @author Bongo
 */
public class Ejemplo {
	int largoUrl;
	int cantH1;
	int cantH2;
	int cantH3;
	int cantH4;
	int cantH5;
	int cantDiv;
	Ejemplo (ProcesadorHTML pagina) {
		largoUrl = pagina.calcularLargoUrl();
		cantH1 = pagina.calcularH1();
		cantH2 = pagina.calcularH2();
		cantH3 = pagina.calcularH3();
		cantH4 = pagina.calcularH4();
		cantH5 = pagina.calcularH5();
		cantDiv = pagina.calcularDiv();
	}
}
