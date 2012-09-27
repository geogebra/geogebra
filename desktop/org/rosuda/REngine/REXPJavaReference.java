package org.rosuda.REngine;

/** REXPJavaReference is a reference to a Java object that has been resolved from is R wrapper. Note that not all engines support references. */
public class REXPJavaReference extends REXP {
	/** the referenced Java object */
	Object object;

	/** creates a new Java reference R object
	 *  @param o Java object referenced by the REXP */
	public REXPJavaReference(Object o) { super(); this.object = o; }

	/** creates a new Java reference R object
	 *  @param o Java object referenced by the REXP
	 *  @param attr attributes (of the R wrapper) */
	public REXPJavaReference(Object o, REXPList attr) { super(attr); this.object = o; }
	
	/** returns the Java object referenced by this REXP
	 *  @return Java object */
	public Object getObject() { return object; }

 	public Object asNativeJavaObject() { return object; }
	
	public String toString() {
		return super.toString() + "[" + object + "]";
	}
}
