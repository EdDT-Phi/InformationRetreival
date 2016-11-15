package ir.vsr;

import java.io.*;
import java.util.*;
import java.lang.*;

import ir.utilities.*;
import ir.classifiers.*;


class PageRankInvertedIndex extends InvertedIndex{

	HashMap<String, Double> weights;
	/**
	* Create an inverted index of the documents in a directory.
	*
	* @param dirFile  The directory of files to index.
	* @param docType  The type of documents to index (See docType in DocumentIterator)
	* @param stem     Whether tokens should be stemmed with Porter stemmer.
  * @param feedback Whether relevance feedback should be used.
	* @param weights Whether relevance feedback should be used.
	*/
	public PageRankInvertedIndex(File dirFile, short docType, boolean stem, boolean feedback, double weight) {
		super(dirFile, docType, stem, feedback);
		readWeights(weight);
	}


  /**
   * Reads weights into the hashmap from file 'page_ranks.txt'
   * Assumes page exists and has correct format
   */
	public void readWeights(double weight) {
		try(Scanner sc = new Scanner(new File("page_ranks.txt"))) {
			weights = new HashMap<String, Double>();
			while(sc.hasNext()) {
				weights.put(sc.next(), Double.parseDouble(sc.next()) * weight);
			}
		} catch (Exception e) {
			System.out.println("An error occured: " + e.toString());
		}
	}

	/**
	 * Perform ranked retrieval on this input query Document vector.
   * Modified to add page rank value.
	 */
	public Retrieval[] retrieve(HashMapVector vector) {
		// Create a hashtable to store the retrieved documents.  Keys
		// are docRefs and values are DoubleValues which indicate the
		// partial score accumulated for this document so far.
		// As each token in the query is processed, each document
		// it indexes is added to this hashtable and its retrieval
		// score (similarity to the query) is appropriately updated.
		Map<DocumentReference, DoubleValue> retrievalHash =
		   new HashMap<DocumentReference, DoubleValue>();
		// Initialize a variable to store the length of the query vector
		double queryLength = 0.0;
		// Iterate through each token in the query input Document
		for (Map.Entry<String, Weight> entry : vector.entrySet()) {
		 String token = entry.getKey();
		 double count = entry.getValue().getValue();
		 // Determine the score added to the similarity of each document
		 // indexed under this token and update the length of the
		 // query vector with the square of the weight for this token.
		 queryLength = queryLength + incorporateToken(token, count, retrievalHash);
		}
		// Finalize the length of the query vector by taking the square-root of the
		// final sum of squares of its token weights.
		queryLength = Math.sqrt(queryLength);
		// Make an array to store the final ranked Retrievals.
		Retrieval[] retrievals = new Retrieval[retrievalHash.size()];
		// Iterate through each of the retrieved documents stored in
		// the final retrievalHash.
		int retrievalCount = 0;
    for (Map.Entry<DocumentReference, DoubleValue> entry : retrievalHash.entrySet()) {
     DocumentReference docRef = entry.getKey();
		 double score = entry.getValue().value + weights.get(docRef.file.getName());
		 retrievals[retrievalCount++] = getRetrieval(queryLength, docRef, score);
		}

		// Sort the retrievals to produce a final ranked list using the
		// Comparator for retrievals that produces a best to worst ordering.
		Arrays.sort(retrievals);
		return retrievals;
		}

	/**
   * Index a directory of files and then interactively accept retrieval queries.
   * Command format: "InvertedIndex [OPTION]* [DIR]" where DIR is the name of
   * the directory whose files should be indexed, and OPTIONs can be
   * "-html" to specify HTML files whose HTML tags should be removed.
   * "-stem" to specify tokens should be stemmed with Porter stemmer.
   * "-feedback" to allow relevance feedback from the user.
   */
  public static void main(String[] args) {
    // Parse the arguments into a directory name and optional flag

    String dirName = args[args.length - 1];
    short docType = DocumentIterator.TYPE_TEXT;
    boolean stem = false, feedback = false;
    double weight = 0;
    for (int i = 0; i < args.length - 1; i++) {
      String flag = args[i];
      if (flag.equals("-html"))
        // Create HTMLFileDocuments to filter HTML tags
        docType = DocumentIterator.TYPE_HTML;
      else if (flag.equals("-stem"))
        // Stem tokens with Porter stemmer
        stem = true;
      else if (flag.equals("-feedback"))
        // Use relevance feedback
        feedback = true;
     	else if (flag.equals("-weight"))
     		weight = Double.parseDouble(args[++i]);
      else {
        throw new IllegalArgumentException("Unknown flag: "+ flag);
      }
    }


    // Create an inverted index for the files in the given directory.
    PageRankInvertedIndex index = new PageRankInvertedIndex(new File(dirName), docType, stem, feedback, weight);
    // index.print();
    // Interactively process queries to this index.
    index.processQueries();
  }
}