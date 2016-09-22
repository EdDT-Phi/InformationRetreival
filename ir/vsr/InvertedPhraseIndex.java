package ir.vsr;

import java.io.*;
import java.util.*;
import java.lang.*;

import ir.utilities.*;
import ir.classifiers.*;

/**
 * An inverted index for vector-space information retrieval. Contains
 * methods for creating an inverted index from a set of documents
 * and retrieving ranked matches to queries using standard TF/IDF
 * weighting and cosine similarity. This version includes the
 * functionality of indexing and querying on bigrams.
 *
 * @author Eddie Tribaldos
 */
public class InvertedPhraseIndex extends InvertedIndex {

	/**
	 * List to store bigrams
	 */
	public HashSet<String> bigrams;

	/**
	 * Number of bigrams to store
	 */
	public final int NUM_BIGRAMS = 1000;

	/**
	 * Create an inverted index of the documents in a directory.
	 *
	 * @param dirFile  The directory of files to index.
	 * @param docType  The type of documents to index (See docType in DocumentIterator)
	 * @param stem     Whether tokens should be stemmed with Porter stemmer.
	 * @param feedback Whether relevance feedback should be used.
	 */
	public InvertedPhraseIndex(File dirFile, short docType, boolean stem, boolean feedback) {
		super(dirFile, docType, stem, feedback);
		bigrams = new HashSet<String>();
	}

  /**
   * Class to be able to sort bigrams as a Collection thanks to Comparable
   */
	public class Bigram implements Comparable<Bigram> {
		String s;
		int n;
		
		public Bigram(String _s, int _n) {
			s = _s;
			n = _n;
		}

		public int compareTo(Bigram bg) {
			return bg.n - n;
		}
	}

	/**
	 * Function to populate bigrams
   * First stores as a hashmap to quickly store count
   * Then stores as a list to nbe able to sort
   * Finally moves to a HashSet for constant time search
	 */
	protected void populateBigrams() {
		DocumentIterator  docIter = new DocumentIterator(dirFile, docType, stem);
		HashMap<String, Integer> tempStore = new HashMap<String, Integer>();

    // Populate hashmap by iteratomg through all the documents
		while (docIter.hasMoreDocuments()) {
			FileDocument doc = docIter.nextDocument();
			doc.hashMapVectorBigrams(tempStore);
		}

		// Convert HashMap to List and sort
		List<Bigram> tempList = new ArrayList<Bigram>();
		for(String s: tempStore.keySet()) {
			tempList.add(new Bigram(s, tempStore.get(s)));
		}
		Collections.sort(tempList);

    // Convert to Hashset
		for(int i = 0; i < NUM_BIGRAMS && i < tempList.size(); i++) {
      if(verbose && i < 100) terminal(tempList.get(i).s +": " + tempList.get(i).n);
			bigrams.add(tempList.get(i).s);
		}
	}


	/**
	 * Index the documents in dirFile. Modified to pass in bigrams
	 */
	protected void indexDocuments() {
		if (!tokenHash.isEmpty() || !docRefs.isEmpty()) {
			// Currently can only index one set of documents when an index is created
			throw new IllegalStateException("Cannot indexDocuments more than once in the same InvertedIndex");
		}
		// Get an iterator for the documents
		DocumentIterator docIter = new DocumentIterator(dirFile, docType, stem);
		System.out.println("Indexing documents in " + dirFile);

		// Loop, processing each of the documents
		while (docIter.hasMoreDocuments()) {
			FileDocument doc = docIter.nextDocument();

			// Create a document vector for this document
			console(doc.file.getName(), false, ',');
			HashMapVector vector = doc.hashMapVector(bigrams);
			indexDocument(doc, vector);
		}

		// Now that all documents have been processed, we can calculate the IDF weights for
		// all tokens and the resulting lengths of all weighted document vectors.
		computeIDFandDocumentLengths();
		System.out.println("\nIndexed " + docRefs.size() + " documents with " + size() + " unique terms.");
	}

	/**
	 * Perform ranked retrieval on this input query Document. Modified to pass in bigrams
	 */
	public Retrieval[] retrieve(Document doc) {
		return retrieve(doc.hashMapVector(bigrams));
	}

	/**
	 * Enter an interactive user-query loop, accepting queries and showing the retrieved
	 * documents in ranked order. Modified to pass in bigrams.
	 */
	public void processQueries() {

		System.out.println("Now able to process queries. When done, enter an empty query to exit.");

		// Loop indefinitely answering queries and get a query from the console
		// If query is empty then exit the interactive loop
		String query;
		while (!(query = UserInput.prompt("\nEnter query:  ")).equals("")) {
			// Get the ranked retrievals for this query string and present them
			HashMapVector queryVector = (new TextStringDocument(query, stem)).hashMapVector(bigrams);
			Retrieval[] retrievals = retrieve(queryVector);
			presentRetrievals(queryVector, retrievals);
		}
	}

	/**
	 * Index a directory of files and then interactively accept retrieval queries.
	 * Command format: "InvertedIndex [OPTION]* [DIR]" where DIR is the name of
	 * the directory whose files should be indexed, and OPTIONs can be
	 * "-html" to specify HTML files whose HTML tags should be removed.
	 * "-stem" to specify tokens should be stemmed with Porter stemmer.
	 * "-feedback" to allow relevance feedback from the user.
   * "-v" to run the program verbosely
	 */
	public static void main(String[] args) {


		// Parse the arguments into a directory name and optional flag
		if(args.length == 0)
			throw new IllegalArgumentException("No corpus directory specified");

		String dirName = args[args.length - 1];
		short docType = DocumentIterator.TYPE_TEXT;
		boolean stem = false, feedback = false;
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
			else if (flag.equals("-v"))
        // Be verbose
				verbose = true;
			else
				throw new IllegalArgumentException("Unknown flag: "+ flag);
		}

    if(!verbose)
      terminal("**use flag -v to see full output**");

		// Create an inverted index for the files in the given directory.
		InvertedPhraseIndex index = new InvertedPhraseIndex(new File(dirName), docType, stem, feedback);
		index.print();

		terminal("Populating Bigrams");
		index.populateBigrams();
		index.indexDocuments();
		index.processQueries();
	}
}
