package ir.classifiers;

import java.io.*;
import java.util.*;

import ir.vsr.*;
import ir.utilities.*;

/**
 * Implements the NaiveBayes Classifier with Laplace smoothing. Stores probabilities
 * internally as logs to prevent underflow problems.
 *
 */
public class KNN extends Classifier {


  InvertedIndex invIndx;
  HashMap<File, Integer> training_examples;
  int k = 5;

  /**
   * Name of classifier
   */
  public static final String name = "KNN";

  /**
   * Number of categories
   */
  int numCategories;

  /**
   * Number of training examples, set by train function
   */
  int numExamples;

  /**
   * Flag for debug prints
   */
  boolean debug = false;

  /**
   * Create a naive Bayes classifier with these attributes
   *
   * @param categories The array of Strings containing the category names
   * @param debug      Flag to turn on detailed output
   */
  public KNN(String[] categories, boolean debug) {
    this.categories = categories;
    this.debug = debug;
    numCategories = categories.length;
  }


  /**
   * Create a naive Bayes classifier with these attributes
   *
   * @param categories The array of Strings containing the category names
   * @param debug      Flag to turn on detailed output
   */
  public KNN(String[] categories, int k, boolean debug) {
    this.categories = categories;
    this.debug = debug;
    this.k = k;
    numCategories = categories.length;
  }

  /**
   * Sets the debug flag
   */
  public void setDebug(boolean bool) {
    debug = bool;
  }

  /**
   * Returns the name
   */
  public String getName() {
    return name + "_K" + k;
  }


  /**
   * Returns training result
   */
  public InvertedIndex getTrainResult() {
    return invIndx;
  }

  /**
   * Trains the Naive Bayes classifier - estimates the prior probs and calculates the
   * counts for each feature in different categories
   *
   * @param trainExamples The vector of training examples
   */
  public void train(List<Example> trainExamples) {
    training_examples = new HashMap<File, Integer>();

    for(Example ex: trainExamples) {
      training_examples.put(ex.document.file, ex.getCategory());
    }

    invIndx = new InvertedIndex(trainExamples);
  }

  /**
   * Categorizes the test example using the trained Naive Bayes classifier, returning true if
   * the predicted category is same as the actual category
   *
   * @param testExample The test example to be categorized
   */
  public boolean test(Example testExample) {
    Retrieval[] results = invIndx.retrieve(testExample.hashVector);

    if(results.length == 0) {
      if(debug) System.out.println("No docs found");
      return false;
    }

    double[] counts = new double[numCategories];
    for(int i = 0; i < k && i < results.length; i++) {
      counts[training_examples.get(results[i].docRef.file)] += 1;
    }

    if(debug) {
      for(int i = 0; i < numCategories; i++) {
        System.out.println("\t" + categories[i] +": " + counts[i]);
      }
    }

    int predictedClass = argMax(counts);
    if (debug) {
      System.out.print("Document: " + testExample.name + "\nResults: ");
      System.out.println("\nCorrect class: " + testExample.getCategory() + ", Predicted class: " + predictedClass + "\n");
    }

    return (predictedClass == testExample.getCategory());
  }
}
