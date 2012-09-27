package org.rosuda.REngine;

/** abstract class representing all vectors in R */
public abstract class REXPVector extends REXP {
	public REXPVector() { super(); }
	
	public REXPVector(REXPList attr) {
		super(attr);
	}

	/** returns the length of the vector (i.e. the number of elements)
	 *  @return length of the vector */
	public abstract int length();

	public boolean isVector() { return true; }

	/** returns a boolean vector of the same length as this vector with <code>true</code> for NA values and <code>false</code> for any other values
	 *  @return a boolean vector of the same length as this vector with <code>true</code> for NA values and <code>false</code> for any other values */
	public boolean[] isNA() {
		boolean a[] = new boolean[length()];
		return a;
	}
	
	public String toString() {
		return super.toString()+"["+length()+"]";
	}
	
	public String toDebugString() {
		return super.toDebugString()+"["+length()+"]";
	}
}
