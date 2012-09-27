package org.rosuda.REngine;

/** represents a NULL object in R.
 <p>
 Note: there is a slight asymmetry - in R NULL is represented by a zero-length pairlist. For this reason <code>REXPNull</code> returns <code>true</code> for {@link #isList()} and {@link #asList()} will return an empty list. Nonetheless <code>REXPList</code> of the length 0 will NOT return <code>true</code> in {@link #isNull()} (currently), becasue it is considered a different object in Java. These nuances are still subject to change, because it's not clear how it should be treated. At any rate use <code>REXPNull</code> instead of empty <code>REXPList</code> if NULL is the intended value.
 */
public class REXPNull extends REXP {
	public REXPNull() { super(); }
	public REXPNull(REXPList attr) { super(attr); }
	
	public boolean isNull() { return true; }
	public boolean isList() { return true; } // NULL is a list
	public RList asList() { return new RList(); }
	public Object asNativeJavaObject() { return null; }
}
