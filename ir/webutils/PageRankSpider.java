package ir.webutils;

import java.util.*;
import java.io.*;

import ir.utilities.*;

class PageRankSpider extends Spider{

	HashMap<Link, HashSet<Link>> page_graph = new HashMap<Link, HashSet<Link>>();
	HashMap<Link, Double> weights = new HashMap<Link, Double>();
	HashMap<Link, String> indexed = new HashMap<Link, String>();
	final double ALPHA = 0.15;
	final double MAX_ITERS = 50;

	public static void main(String[] args) {
		new PageRankSpider().go(args);
	}

	/**
   * Checks command line arguments and performs the crawl.  <p> This
   * implementation calls <code>processArgs</code> and
   * <code>doCrawl</code>.
   *
   * @param args Command line arguments.
   */
  public void go(String[] args) {
    processArgs(args);
    doCrawl();
    printGraph();
    pageRank();
  }

  public void printGraph() {
  	System.out.println("Graph structure: ");
  	for(Link link: page_graph.keySet()) {
  		System.out.println(link + "->" + new LinkedList(page_graph.get(link)));
  	}
  }

	/**
   * Returns a list of links to follow from a given page.
   * Subclasses can use this method to direct the spider's path over
   * the web by returning a subset of the links on the page.
   *
   * @param page The current page.
   * @return Links to be visited from this page
   */
	protected List<Link> getNewLinks(HTMLPage page) {
	  	List<Link> links = new LinkExtractor(page).extractLinks();

	  	if(page_graph.get(page.link) == null){
	  		page_graph.put(page.link, new HashSet<Link>());
	  	}

	  	for(Link l: links){
	  		page_graph.get(page.link).add(l);
	  	}

	  return links;
	}

	protected void pageRank() {
		for(Link l: page_graph.keySet()) {
			if(indexed.get(l) != null)
				weights.put(l, (1 / (double) page_graph.size()));
		}

		double e_p = ALPHA / weights.size();
		for (int i = 0; i < MAX_ITERS; i++) {
			runPageRank(e_p);
		}

		try(FileWriter fw = new FileWriter(new File("page_ranks.txt"))) {
			TreeMap<String, Link> order = new TreeMap<String, Link>();
			for(Link link: indexed.keySet())
				order.put(indexed.get(link), link);

			System.out.println("Page rank:");
			for(String str: order.keySet()){
				System.out.println(order.get(str) + ": " + weights.get(order.get(str)));
				fw.write(str+ " " + weights.get(order.get(str)) + "\n");
			}
			
		} catch (Exception e) {
			System.out.println("An error occured: " + e.toString());
		}
	}

	protected void runPageRank(double e_p) {
		HashMap<Link, Double> new_weights = new HashMap<Link, Double>();
		double c = 0;
		for(Link curr: weights.keySet()) {
			if(page_graph.get(curr) == null) continue;
			double mod = (1 - ALPHA) / page_graph.get(curr).size();
			for(Link link: page_graph.get(curr)){
				if(new_weights.get(link) == null) {
					new_weights.put(link, e_p);
					c += e_p;
				}

				c += weights.get(curr) * mod;
				new_weights.put(link,  weights.get(curr) * mod + new_weights.get(link));
			}
		}

		for(Link link: new_weights.keySet()) {
			weights.put(link, new_weights.get(link) / c);
		}
	}

	/**
   * "Indexes" a <code>HTMLpage</code>.  This version just writes it
   * out to a file in the specified directory with a "P<count>.html" file name.
   *
   * @param page An <code>HTMLPage</code> that contains the page to
   *             index.
   */
  	protected void indexPage(HTMLPage page) {
  		String name = "P" + MoreString.padWithZeros(count, (int) Math.floor(MoreMath.log(maxCount, 10)) + 1);
   	page.write(saveDir, name);
   	indexed.put(page.link, name+".html");
  }
}