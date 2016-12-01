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

  /**
   * Inverted index that holds examples
   */
  InvertedIndex invIndx;

  /**
   * Used to map results to categories
   */
  HashMap<File, Integer> training_examples;

  /**
   * Number of results to consider
   */
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
   * Returns the name
   */
  public String getName() {
    return name + "_K" + k;
  }


  /**
   * Trains the KNN classifier - create the inverted index and map
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
   * Categorizes the test example using the trained KNN classifier, returning true if
   * the predicted category is same as the actual category
   *
   * @param testExample The test example to be categorized
   */
  public boolean test(Example testExample) {
    Retrieval[] results = invIndx.retrieve(testExample.hashVector);
    
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
