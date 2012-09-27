package org.rosuda.REngine;

/** REXPSymbol represents a symbol in R. */
public class REXPSymbol extends REXP {
	/** name of the symbol */
	private String name;
	
	/** create a new symbol of the given name */
	public REXPSymbol(String name) {
		super();
		this.name=(name==null)?"":name;
	}
	
	public boolean isSymbol() { return true; }
	
	/** returns the name of the symbol
	 *  @return name of the symbol */
	public String asString() { return name; }

	public String[] asStrings() {
		return new String[] { name };
	}
	
	public String toString() {
		return getClass().getName()+"["+name+"]";
	}

	public String toDebugString() {
		return super.toDebugString()+"["+name+"]";
	}

	public Object asNativeJavaObject() {
		return name;
	}
}
