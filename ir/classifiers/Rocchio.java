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
public class Rocchio extends Classifier {

  InvertedIndex invIndx;
  HashMapVector[] vectors;
  boolean neg = false;


  /**
   * Name of classifier
   */
  public static final String name = "Rocchio";

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
  public Rocchio(String[] categories, boolean debug) {
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
  public Rocchio(String[] categories, boolean neg, boolean debug) {
    this.categories = categories;
    this.debug = debug;
    this.neg = neg;
    numCategories = categories.length;
  }

  /**
   * Returns the name
   */
  public String getName() {
    return name + (neg ? "_neg" : "");
  }

  /**
   * Trains the Naive Bayes classifier - estimates the prior probs and calculates the
   * counts for each feature in different categories
   *
   * @param trainExamples The vector of training examples
   */
  public void train(List<Example> trainExamples) {

    vectors = new HashMapVector[numCategories];
    invIndx = new InvertedIndex(trainExamples);
    for(int i = 0; i < numCategories; i++) {
      vectors[i] = new HashMapVector();
    }
    for(Example ex: trainExamples){
      // Scale vector by max weight, using idf's from inverted index
      vectors[ex.getCategory()].addScaled(ex.hashVector, 1/ex.hashVector.maxWeight(), invIndx);
    }

    if(neg) {
      // Do negative reinforcement
      for(Example ex: trainExamples){
        for(int i = 0; i < numCategories; i++) {
          // Skip example's category
          if(i == ex.getCategory()) continue;

          // Scale vector by max weight, using idf's from inverted index
          vectors[i].addScaled(ex.hashVector, -1/ex.hashVector.maxWeight(), invIndx);
        }
      }
    }
  }

  /**
   * Categorizes the test example using the trained Naive Bayes classifier, returning true if
   * the predicted category is same as the actual category
   *
   * @param testExample The test example to be categorized
   */
  public boolean test(Example testExample) {
    double[] cos_sim = new double[numCategories];
    for(int i = 0; i < numCategories; i++) {
      cos_sim[i] = testExample.hashVector.cosineTo(vectors[i]);
    }

    if(debug) {
      for(int i = 0; i < numCategories; i++) {
        System.out.println("\t" + categories[i] +": " + cos_sim[i]);
      }
    }

    int predictedClass = argMax(cos_sim);
    if (debug) {
      System.out.print("Document: " + testExample.name + "\nResults: ");
      System.out.println("\nCorrect class: " + testExample.getCategory() + ", Predicted class: " + predictedClass + "\n");
    }

    return (predictedClass == testExample.getCategory());
  }
}
