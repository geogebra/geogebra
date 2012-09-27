package org.rosuda.REngine;

/** REXPUnknown is a stand-in for an object that cannot be represented in the REngine hierarchy. Usually this class can be encountered when new types are introduced in R or if a given engine doesn't support all data types. Usually REXPUnknown can only be retrieved from the engine but never assigned. */
public class REXPUnknown extends REXP {
	/** type of the unterlying obejct */
	int type;

	/** creates a new unknown object of the given type
	 *  @param type internal R type code of this object */
	public REXPUnknown(int type) { super(); this.type=type; }

	/** creates a new unknown object of the given type
	 *  @param type internal R type code of this object
	 *  @param attr attributes */
	public REXPUnknown(int type, REXPList attr) { super(attr); this.type=type; }
	
	/** returns the internal R type of this unknown obejct
	 *  @return type code */
	public int getType() { return type; }
	
	public String toString() {
		return super.toString()+"["+type+"]";
	}
}
