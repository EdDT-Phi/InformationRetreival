package ir.webutils;

import java.util.*;
import java.io.*;
import java.net.*;

import ir.utilities.*;

class PageRankSiteSpider extends PageRankSpider {

   /**
    * The domain of the first link. New links are compared to this
    */
   String domain;

     /**
   * Spider the web according to the following command options:
   * <ul>
   * <li>-safe : Check for and obey robots.txt and robots META tag
   * directives.</li>
   * <li>-d &lt;directory&gt; : Store indexed files in &lt;directory&gt;.</li>
   * <li>-c &lt;maxCount&gt; : Store at most &lt;maxCount&gt; files (default is 10,000).</li>
   * <li>-u &lt;url&gt; : Start at &lt;url&gt;. And restrict spidering to this domain.</li>
   * <li>-slow : Pause briefly before getting a page.  This can be
   * useful when debugging.
   * </ul>
   */
   public static void main(String[] args) {
      new PageRankSiteSpider().go(args);
   }

   /**
   * Returns a list of links to follow from a given page.
   * This subclasses uses this method to direct the spider's path over
   * the web by returning a subset of the links on the page based on the domain.
   *
   * @param page The current page.
   * @return Links to be visited from this page
   */
   protected List<Link> getNewLinks(HTMLPage page) {
      List<Link> links = new LinkExtractor(page).extractLinks();

      // used to build page graph
      if(page_graph.get(page.link) == null){
         page_graph.put(page.link, new HashSet<Link>());
      }

      // filter out links not in domain
      List<Link> response = new LinkedList<Link>();
      for(Link l: links) {
         if(isInDomain(l)) {
            response.add(l);
            page_graph.get(page.link).add(l);
         }
      }

     return response;
   }

   /**
    * Compares domain value to new link domains
    */
   private boolean isInDomain(Link l) {
      return l.getURL().getHost().equals(domain);
   }

   /**
   * Called when "-u" is passed in on the command line.  <p> This
   * implementation adds <code>value</code> to the list of links to
   * visit and saves the domain for future comparisons.
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