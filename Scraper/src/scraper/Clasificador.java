/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
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
	Instances trainUltimosPartidos;
	Instances test;
	FilteredClassifier fc;
	J48 j48;

	Clasificador(String archTrain, String archTest) {
		try {
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(archTrain));
			train = loader.getDataSet();
			if (train.classIndex() == -1) {
				train.setClassIndex(train.numAttributes() - 1);
			}

			loader = new CSVLoader();
			loader.setSource(new File(archTest));
			test = loader.getDataSet();
			if (test.classIndex() == -1) {
				test.setClassIndex(test.numAttributes() - 1);
			}
			System.out.println("toy aca1");
			trainUltimosPartidos = new Instances(train, 3000);
			System.out.println("toy aca2");
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

	void agregarInstanciaUltimosPartidos(Instance ins) {
		int maxCantPartidos = 3;
		int indexLocal = 0;
		int indexVisitante = 1;

		System.out.println(trainUltimosPartidos.numInstances());

//		String visitante = test.attribute(1).value(ins.attribute(1).toString());
		String local = ins.attribute(indexLocal).value((int) ins.value(indexLocal));
		String visitante = ins.attribute(indexVisitante).value((int) ins.value(indexVisitante));

		int cantLocal = 0;
		int cantVisitante = 0;

		for (int i = 0; i < trainUltimosPartidos.numInstances(); i++) {
			if (trainUltimosPartidos.instance(i).value(indexLocal) == ins.value(indexLocal)) {
				cantLocal++;
			}
			if (trainUltimosPartidos.instance(i).value(indexVisitante) == ins.value(indexVisitante)) {
				cantVisitante++;
			}
		}

		int i = 0;
		boolean encontreL = cantLocal < maxCantPartidos;
		boolean encontreV = cantVisitante < maxCantPartidos;
		int numL = 0;
		int numV = 0;
		while (i < trainUltimosPartidos.numInstances() && (!encontreL || !encontreV)) {
			if (!encontreL && trainUltimosPartidos.instance(i).value(indexLocal) == ins.value(indexLocal)) {
				numL = i;
				encontreL = true;
			}
			if (!encontreV && trainUltimosPartidos.instance(i).value(indexVisitante) == ins.value(indexVisitante)) {
				numV = i;
				encontreV = true;
			}
			i++;
		}
		if (!(cantLocal < maxCantPartidos)) {
			trainUltimosPartidos.delete(numL);
		}

		if ((!(cantVisitante < maxCantPartidos) && numL != numV) || (!(cantVisitante < maxCantPartidos) && (cantLocal < maxCantPartidos))) {
			encontreV = cantVisitante < maxCantPartidos;
			i = 0;
			while (i < trainUltimosPartidos.numInstances() && !encontreV) {
				if (!encontreV && trainUltimosPartidos.instance(i).value(indexVisitante) == ins.value(indexVisitante)) {
					numV = i;
					encontreV = true;
				}
				i++;
			}
			trainUltimosPartidos.delete(numV);
		}

		trainUltimosPartidos.add(ins);

//		Instance ins2 = new Instance(ins);
//		ins2.setValue(indexLocal, ins.value(indexVisitante));
//		ins2.setValue(indexVisitante, ins.value(indexLocal));
//		trainUltimosPartidos.add(ins2);

	}

	void crearModelo2() {
		try {
			j48 = new J48();
			String[] optionsj48 = weka.core.Utils.splitOptions("-C 0.20 -M 2");
			j48.setUnpruned(true);
//			j48.setOptions(optionsj48);
			j48.buildClassifier(trainUltimosPartidos);


			IBk knn = new IBk();
			knn.setOptions(weka.core.Utils.splitOptions("-K 3 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.ChebyshevDistance -R first-last\\\"\""));


			fc = new FilteredClassifier();
			//fc.setFilter(rm);
			fc.setClassifier(j48);
			// train and make predictions
			fc.buildClassifier(trainUltimosPartidos);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	double clasificar2(Instance ins) {
		int indexLocal = 0;
		int indexVisitante = 1;

		int cantLocal = 0;
		int cantVisitante = 0;

		for (int i = 0; i < train.numInstances(); i++) {
			if (train.instance(i).value(indexLocal) == ins.value(indexLocal)) {
				cantLocal++;
			}
			if (train.instance(i).value(indexVisitante) == ins.value(indexVisitante)) {
				cantVisitante++;
			}
		}
		String ultLocal = "";
		double ultLocalDouble = 0;
		String ultVisitante = "";
		double ultVisitanteDouble = 0;
		int i = 0;
		int cantLocalAux = 0;
		int cantVisitanteAux = 0;
		while (i < train.numInstances()) {
			if (train.instance(i).value(indexLocal) == ins.value(indexLocal)) {
				cantLocalAux++;
				if (cantLocalAux == cantLocal) {
					ultLocal = train.classAttribute().value((int) train.instance(i).classValue());
					ultLocalDouble = train.instance(i).classValue();
				}
			}
			if (train.instance(i).value(indexVisitante) == ins.value(indexVisitante)) {
				cantVisitanteAux++;
				if (cantVisitanteAux == cantVisitante) {
					ultVisitante = train.classAttribute().value((int) train.instance(i).classValue());
					ultVisitanteDouble = train.instance(i).classValue();
				}
			}
			i++;
		}

		double ret = 2;

		if (ultLocal.equals("L") && ultVisitante.equals("L")) {
			ret = 2;
			System.out.println("Local: " + ultLocalDouble);
		} else if (ultLocal.equals("V") && ultVisitante.equals("V")) {
			ret = 0;
			System.out.println("Visitante: " + ultLocalDouble);
		} else if (ultLocal.equals("E") && ultVisitante.equals("E")) {
			System.out.println("Empate: " + ret);
			ret = 0;
		} else {
			try {
				ret = fc.classifyInstance(ins);
			} catch (Exception e) {
			}
		}
		return ret;
	}

	void clasificar() {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream("fscore.csv", false), Charset.forName("ISO-8859-15"));
			out.write("Tamaño del corpus; FscoreL; FscoreE; FscoreV");
			out.write(System.getProperty("line.separator"));
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			double presicionL = 0;
			double presicionE = 0;
			double presicionV = 0;
			double recallL = 0;
			double recallE = 0;
			double recallV = 0;
			double fscoreL = 0;
			double fscoreE = 0;
			double fscoreV = 0;

			int indexLocal = 0;
			int indexVisitante = 1;
			int malClasificados = 0;
			int cantTotal = 0;
			double[][] matrizConfusion = new double[3][3];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					matrizConfusion[i][j] = 0;
				}
			}

			for (int i = 0; i < test.numInstances(); i++) {

				System.out.println(train.numInstances());
				System.out.println(test.instance(i));

//				Enumeration en = train.attribute(indexLocal).enumerateValues();
//				String local = test.attribute(indexLocal).value((int) test.instance(i).value(indexLocal));
//				boolean encontreLocal = false;
//				while (en.hasMoreElements() && !encontreLocal) {
//					if (local.equals(en.nextElement())) {
//						encontreLocal = true;
//					}
//				}
//
//				if(!encontreLocal){
//					System.out.println("Local::::::::::::::::::::::::::::"+local);
//					train.attribute(indexLocal).addStringValue(local);
//				}
//
//				en = train.attribute(indexVisitante).enumerateValues();
//				String visitante = test.attribute(indexVisitante).value((int) test.instance(i).value(indexVisitante));
//				boolean encontreVisitante = false;
//				while (en.hasMoreElements() && !encontreVisitante) {
//					if (visitante.equals(en.nextElement())) {
//						encontreVisitante = true;
//					}
//				}
//
//				if(!encontreVisitante){
//					System.out.println("Visitante::::::::::::::::::::::::::::"+visitante);
//					train.attribute(indexVisitante).addStringValue(visitante);
//				}

//				if (encontreLocal && encontreVisitante) {					
				cantTotal++;
//				double pred = fc.classifyInstance(test.instance(i));
				double pred = fc.classifyInstance(test.instance(i));

				//System.out.print("Num: " + i);
				//				System.out.print(test.instance(i) + " ");
				String valorActual = test.classAttribute().value((int) test.instance(i).classValue());
				//				System.out.print(", actual: " + valorActual);
				String valorPredicho = test.classAttribute().value((int) pred);
				//				System.out.println(", predicted: " + valorPredicho);
				if (!valorActual.equals(valorPredicho)) {
					malClasificados++;
				}
				if (valorActual.equals("L")) {
					if (valorPredicho.equals("L")) {
						matrizConfusion[0][0]++;
					} else if (valorPredicho.equals("E")) {
						matrizConfusion[0][1]++;
					} else {
						matrizConfusion[0][2]++;
					}
				} else if (valorActual.equals("E")) {
					if (valorPredicho.equals("L")) {
						matrizConfusion[1][0]++;
					} else if (valorPredicho.equals("E")) {
						matrizConfusion[1][1]++;
					} else {
						matrizConfusion[1][2]++;
					}
				} else {
					if (valorPredicho.equals("L")) {
						matrizConfusion[2][0]++;
					} else if (valorPredicho.equals("E")) {
						matrizConfusion[2][1]++;
					} else {
						matrizConfusion[2][2]++;
					}
				}




				//train.delete(0);

//				train.add(test.instance(i));
//				this.crearModelo();


				train.add(test.instance(i));
				this.crearModelo();
//				}





				double error = (double) malClasificados * 100.0 / (double) cantTotal;
				System.out.println("Error: " + twoDForm.format(error) + "%");

				presicionL = matrizConfusion[0][0] / (matrizConfusion[0][0] + matrizConfusion[1][0] + matrizConfusion[2][0]);
				presicionE = matrizConfusion[1][1] / (matrizConfusion[1][1] + matrizConfusion[0][1] + matrizConfusion[2][1]);
				presicionV = matrizConfusion[2][2] / (matrizConfusion[2][2] + matrizConfusion[0][2] + matrizConfusion[1][2]);


				recallL = matrizConfusion[0][0] / (matrizConfusion[0][0] + matrizConfusion[0][1] + matrizConfusion[0][2]);
				recallE = matrizConfusion[1][1] / (matrizConfusion[1][1] + matrizConfusion[1][0] + matrizConfusion[1][2]);
				recallV = matrizConfusion[2][2] / (matrizConfusion[2][2] + matrizConfusion[2][0] + matrizConfusion[2][1]);


				fscoreL = 2 * presicionL * recallL / (presicionL + recallL);
				fscoreE = 2 * presicionE * recallE / (presicionE + recallE);
				fscoreV = 2 * presicionV * recallV / (presicionV + recallV);


				// Esto es para generar un CSV con el fscore

				out.write(train.numInstances() + ";" + twoDForm.format(fscoreL) + ";" + twoDForm.format(fscoreE) + ";" + twoDForm.format(fscoreV));
				out.write(System.getProperty("line.separator"));


			}

			System.out.println("presicionL: " + twoDForm.format(presicionL));
			System.out.println("recallL: " + twoDForm.format(recallL));
			System.out.println("fscoreL: " + twoDForm.format(fscoreL));
			System.out.println("presicionE: " + twoDForm.format(presicionE));
			System.out.println("recallE: " + twoDForm.format(recallE));
			System.out.println("fscoreE: " + twoDForm.format(fscoreE));
			System.out.println("presicionV: " + twoDForm.format(presicionV));
			System.out.println("recallV: " + twoDForm.format(recallV));
			System.out.println("fscoreV: " + twoDForm.format(fscoreV));

			out.close();
		} catch (Exception e) {
			System.out.println("Error en weka: " + e.toString());
		}
	}
}

