/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package temaDeLaSemana;

import java.io.File;
import java.util.Collection;
import weka.clusterers.EM;
import weka.core.Instance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author Rodrigo
 */
public class Clustering {

	Instances train;
	EM em;

	Clustering(String archTrain) {
		try {
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(archTrain));
			System.out.println("Clustering: empiezo a cargar los datos");
			train = loader.getDataSet();
//			if (train.classIndex() == -1) {
//				train.setClassIndex(train.numAttributes() - 1);
//			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	void crearModelo() {
		try {
			em = new EM();
			em.setOptions(weka.core.Utils.splitOptions("weka.clusterers.EM -I 100 -N 10 -M 1.0E-6 -S 100"));
			System.out.println("Clustering: empiezo a crear modelo");
			em.buildClusterer(train);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	int clasificar(Collection<Double> vectNoticia) {
		try {
			double [] values = new double [train.numAttributes()];
			int i = 0;
			for (Double val: vectNoticia){
				values[i++] = val;
			}
			
			Instance ins = new Instance(1.0, values);
			System.out.println("Clustering: clasifico una nueva instancia");
			return em.clusterInstance(ins);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return -1;
	}
}
