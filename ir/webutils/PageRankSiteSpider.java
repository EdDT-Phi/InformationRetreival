package ir.webutils;

import java.util.*;
import java.io.*;
import java.net.*;

import ir.utilities.*;

class PageRankSiteSpider extends PageRankSpider {


	String domain;

	public static void main(String[] args) {
		new PageRankSiteSpider().go(args);
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
	  	List<Link> response = new LinkedList<Link>();
	  	for(Link l: links){
	  		if(isInDomain(l)) {
	  			response.add(l);
		  		page_graph.get(page.link).add(l);
	  		}
	  	}

	  return response;
	}

	private boolean isInDomain(Link l) {
		return l.getURL().getHost().equals(domain);
	}

	/**
   * Called when "-u" is passed in on the command line.  <p> This
   * implementation adds <code>value</code> to the list of links to
   * visit.
   *
   * @param value The value associated with the "-u" option.
   */
  protected void handleUCommandLineOption(String value) {
  	try{

    URL url = new URL(value);
    domain = url.getHost();
    linksToVisit.add(new Link(value));
 }catch (Exception e) {
 	System.out.println("An error occured: " + e.toString());
 }
  }
}