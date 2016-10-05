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
    super(dirFile, docType, stem, feedback, false, 1, 1, 1, 1);
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
   * Index the documents in dirFile.
   */
  protected void indexDocuments() {
    bigrams = new HashSet<String>();
    populateBigrams();
    super.indexDocuments();
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

  public HashMapVector getHashMapVector(Document doc) {
    return doc.hashMapVector(bigrams);
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
    // index.print();

		terminal("Populating Bigrams");
		index.processQueries();
	}
}
