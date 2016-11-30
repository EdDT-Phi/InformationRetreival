package ir.classifiers;

import java.util.*;

/**
 * Wrapper class to test NaiveBayes classifier using 10-fold CV.
 * Running it with -debug option gives very detailed output
 *
 * @author Sugato Basu
 */

public class TestKNN {
  /**
   * A driver method for testing the NaiveBayes classifier using
   * 10-fold cross validation.
   *
   * @param args a list of command-line arguments.  Specifying "-debug"
   *             will provide detailed output
   */
  public static void main(String args[]) throws Exception {
    String dirName = "/u/mooney/ir-code/corpora/dmoz-science/";
    String[] categories = {"bio", "chem", "phys"};
    System.out.println("Loading Examples from " + dirName + "...");
    List<Example> examples = new DirectoryExamplesConstructor(dirName, categories).getExamples();
    boolean debug = false;
    int k = 5;
    for(int i = 0; i < args.length; i++) {
      switch(args[i].toLowerCase()){
        case "-debug":
          debug = true;
          break;
        case "-k":
          k = Integer.parseInt(args[++i]);
          break;
        default:
          System.out.println("Unknown flag: " + args[i]);
      }
    }

    KNN classifier = new KNN(categories, k, debug);
    System.out.println("Initializing " +classifier.getName()+ " classifier...");
    CVLearningCurve cvCurve = new CVLearningCurve(classifier, examples);

    // Perform 10-fold cross validation to generate learning curve
    cvCurve.run();
  }
}
