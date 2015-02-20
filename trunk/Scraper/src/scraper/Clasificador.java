/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.File;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
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

            //lazy knn
            IBk knn = new IBk();

            knn.setOptions(weka.core.Utils.splitOptions("-K 1 -W 0 -A \"weka.core.neighboursearch."
                    + "LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\""));
			//knn.setWindowSize(60);

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
        double[] values = new double[train.numAttributes()];
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

    void probar(String archTest) {
        int malClasificados = 0;
        int total = 0;
        try {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(archTest));
            Instances test = loader.getDataSet();
            if (test.classIndex() == -1) {
                test.setClassIndex(test.numAttributes() - 1);
            }

            for (int i = 0; i < test.numInstances(); i++) {
                Instance ins = test.instance(i);
                double pred = fc.classifyInstance(ins);
                String valorActual = test.classAttribute().value((int) test.instance(i).classValue());
                //				System.out.print(", actual: " + valorActual);
                String valorPredicho = test.classAttribute().value((int) pred);
                if (!valorActual.equals(valorPredicho)) {
                    malClasificados++;
                }
                total++;
            }
            System.out.print(malClasificados);
            System.out.print("/");
            System.out.println(total);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
