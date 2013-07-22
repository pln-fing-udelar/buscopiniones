/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LADTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Remove;

/**
 *
 * @author Bongo
 */
public class Clasificador {

	Instances train;
	FilteredClassifier fc;
	J48 j48;

	Clasificador(String archTrain) {
		try {
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(archTrain));
			train = loader.getDataSet();
			if (train.classIndex() == -1) {
				train.setClassIndex(train.numAttributes() - 1);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	void crearModelo() {
		try {
			// filter
			Remove rm = new Remove();
			rm.setAttributeIndices("1");  // remove 1st attribute

			LWL lwl = new LWL();
//			String[] optionslwl = weka.core.Utils.splitOptions("-C 0.20 -M 2");
//			lwl.setOptions(optionslwl);

			// classifier
			J48 j48 = new J48();
			//j48.setUnpruned(true);        // using an unpruned J48
			String[] optionsj48 = weka.core.Utils.splitOptions("-C 0.20 -M 2");
			j48.setOptions(optionsj48);

			LADTree lad = new LADTree();

			ClassificationViaRegression clasif = new ClassificationViaRegression();
			String[] optionsclasif = weka.core.Utils.splitOptions("-W weka.classifiers.trees.M5P -- -M 4.0");
			clasif.setOptions(optionsclasif);

			MultilayerPerceptron redNeuronal = new MultilayerPerceptron();
			String[] optionsred = weka.core.Utils.splitOptions("-L 0.5 -M 0 -N 10000 -V 0 -S 0 -E 5 -H 4 -R");
			redNeuronal.setOptions(optionsred);

			//bayesiano comun
			NaiveBayes bayesiano = new NaiveBayes();
			bayesiano.setUseKernelEstimator(true);

			//lazy knn
			IBk knn = new IBk();
			
			knn.setOptions(weka.core.Utils.splitOptions("-K 4 -W 0 -A \"weka.core.neighboursearch."
					+ "LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\""));
			//knn.setWindowSize(60);
			Vote votacion = new Vote();

			String[] optionsvotacion = weka.core.Utils.splitOptions("weka.classifiers.meta.Vote -S 1 -B \"weka.classifiers.trees.J48 -C 0.2 -M 2\" -B \"weka.classifiers.lazy.IBk -K 7 -W 0 -A \\\"weka.core.neighboursearch.LinearNNSearch -A \\\\\\\"weka.core.ChebyshevDistance -R first-last\\\\\\\"\\\"\" -B \"weka.classifiers.bayes.NaiveBayes \" -B \"weka.classifiers.functions.MultilayerPerceptron -L 0.5 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a\" -R MED");
			votacion.setOptions(optionsvotacion);

			BFTree bf = new BFTree();
			String[] optionsBFTree = weka.core.Utils.splitOptions("-S 1 -M 2 -N 5 -C 1.0 -P POSTPRUNED");
			bf.setOptions(optionsBFTree);

			NaiveBayes naive = new NaiveBayes();
			naive.setUseSupervisedDiscretization(true);

			// meta-classifier
			fc = new FilteredClassifier();
			//fc.setFilter(rm);
			fc.setClassifier(knn);
			// train and make predictions
			fc.buildClassifier(train);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}


	boolean clasificar(Ejemplo ej) throws Exception {
		double [] values = new double [train.numAttributes()];
		values[0] = ej.getTamanioTotalHTML();
		values[1] = ej.getLargoUrl();
		values[2] = ej.getCantH1();
		values[3] = ej.getCantH2();
		values[4] = ej.getCantH3();
		values[5] = ej.getCantH4();
		values[6] = ej.getCantH5();
		values[7] = ej.getCantP();
		values[8] = ej.getCantTable();
		values[9] = ej.getCantDiv();
		values[10] = ej.getCantTags();
		System.out.println("estoy en clasificar1");
		Instance ins = new Instance(1.0, values);
		ins.setDataset(train);
		System.out.println("estoy en clasificar2");
		double pred = fc.classifyInstance(ins);
		System.out.println("estoy en clasificar3");
		String valorPredicho = train.classAttribute().value((int) pred);
		System.out.println("estoy en clasificar4");
		return valorPredicho.equals("true");
	}
}

