package org.rosuda.REngine;

/** Represents a pairlist in R. Unlike the actual internal R implementation this one
    does not use CAR/CDR/TAG linked representation but a @link{RList} object. */
public class REXPList extends REXPVector {
	private RList payload;
	
	/* create a new pairlist with the contents of a named R list and no attributes.
	   @param list named list with the contents */
	public REXPList(RList list) {
		super();
		payload=(list==null)?new RList():list;
	}

	/* create a new pairlist with the contents of a named R list and attributes.
	   @param list named list with the contents
	   @param attr attributes */
	public REXPList(RList list, REXPList attr) {
		super(attr);
		payload=(list==null)?new RList():list;
	}

	/* create a pairlist containing just one pair comprising of one value and one name.
	   This is a convenience constructor most commonly used to create attribute pairlists.
	   @param value of the element in the pairlist (must not be <code>null</code>)
	   @param name of the element in the pairlist (must not be <code>null</code>) */
	public REXPList(REXP value, String name) {
		super();
		payload = new RList(new REXP[] { value }, new String[] { name });
	}

	public Object asNativeJavaObject() throws REXPMismatchException {
		// since REXPGenericVector does the hard work, we just cheat and use it in turn
		REXPGenericVector v = new REXPGenericVector(payload);
		return v.asNativeJavaObject();
	}

	public int length() { return payload.size(); }

	public boolean isList() { return true; }
	public boolean isPairList() { return true; }

	public boolean isRecursive() { return true; }

	public RList asList() { return payload; }
	
	public String toString() {
		return super.toString()+(asList().isNamed()?"named":"");
	}
	
	public String toDebugString() {
		StringBuffer sb = new StringBuffer(super.toDebugString()+"{");
		int i = 0;
		while (i < payload.size() && i < maxDebugItems) {
			if (i>0) sb.append(",\n");
			String name = payload.keyAt(i);
			if (name!=null) sb.append(name+"=");
			sb.append(payload.at(i).toDebugString());
			i++;
		}
		if (i < payload.size()) sb.append(",..");
		return sb.toString()+"}";
	}
}
