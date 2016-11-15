package ir.webutils;

import java.util.*;
import java.io.*;

import ir.utilities.*;

class PageRankSpider extends Spider{


	HashMap<Link, HashSet<Link>> links;

	public static void main(String[] args) {
		new Spider().go(args);
	}
}