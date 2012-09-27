package org.rosuda.REngine;

/** REXPString represents a character vector in R. */
public class REXPString extends REXPVector {
	/** payload */
	private String[] payload;
	
	/** create a new character vector of the length one
	 *  @param load first (and only) element of the vector */
	public REXPString(String load) {
		super();
		payload=new String[] { load };
	}

	/** create a new character vector
	 *  @param load string elements of the vector */
	public REXPString(String[] load) {
		super();
		payload=(load==null)?new String[0]:load;
	}

	/** create a new character vector with attributes
	 *  @param load string elements of the vector
	 *  @param attr attributes */
	public REXPString(String[] load, REXPList attr) {
		super(attr);
		payload=(load==null)?new String[0]:load;
	}
	
	public int length() { return payload.length; }

	public boolean isString() { return true; }

	public Object asNativeJavaObject() {
		return payload;
	}

	public String[] asStrings() {
		return payload;
	}
	
	public boolean[] isNA() {
		boolean a[] = new boolean[payload.length];
		int i = 0;
		while (i < a.length) { a[i] = (payload[i]==null); i++; }
		return a;
	}
	
	public String toDebugString() {
		StringBuffer sb = new StringBuffer(super.toDebugString()+"{");
		int i = 0;
		while (i < payload.length && i < maxDebugItems) {
			if (i>0) sb.append(",");
			sb.append("\""+payload[i]+"\"");
			i++;
		}
		if (i < payload.length) sb.append(",..");
		return sb.toString()+"}";
	}
}
