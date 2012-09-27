package org.rosuda.REngine;

/** represents a language object in R */
public class REXPLanguage extends REXPList {
	public REXPLanguage(RList list) { super(list); }
	public REXPLanguage(RList list, REXPList attr) { super(list, attr); }
	
	public boolean isLanguage() { return true; }
}
