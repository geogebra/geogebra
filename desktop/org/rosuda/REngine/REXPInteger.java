package org.rosuda.REngine;

/** REXPDouble represents a vector of integer values. */
public class REXPInteger extends REXPVector {
	protected int[] payload;
	
	/** NA integer value as defined in R. Unlike its real equivalent this one can be used in comparisons, although {@link #isNA(int) } is provided for consistency. */
	public static final int NA = -2147483648;

	public static boolean isNA(int value) {
		return (value == NA);
	}
	
	/** create integer vector of the length 1 with the given value as its first (and only) element */
	public REXPInteger(int load) {
		super();
		payload=new int[] { load };
	}
	
	/** create integer vector with the payload specified by <code>load</code> */
	public REXPInteger(int[] load) {
		super();
		payload=(load==null)?new int[0]:load;
	}

	/** create integer vector with the payload specified by <code>load</code> and attributes <code>attr</code> */
	public REXPInteger(int[] load, REXPList attr) {
		super(attr);
		payload=(load==null)?new int[0]:load;
	}

	public Object asNativeJavaObject() {
		return payload;
	}
	
	public int length() { return payload.length; }

	public boolean isInteger() { return true; }
	public boolean isNumeric() { return true; }

	public int[] asIntegers() { return payload; }

	/** returns the contents of this vector as doubles */
	public double[] asDoubles() {
		double[] d = new double[payload.length];
		int i = 0;
		while (i < payload.length) { d[i] = (double) payload[i]; i++; }
		return d;
	}

	/** returns the contents of this vector as strings */
	public String[] asStrings() {
		String[] s = new String[payload.length];
		int i = 0;
		while (i < payload.length) { s[i] = ""+payload[i]; i++; }
		return s;
	}
	
	public boolean[] isNA() {
		boolean a[] = new boolean[payload.length];
		int i = 0;
		while (i < a.length) { a[i] = (payload[i]==NA); i++; }
		return a;
	}
	
	public String toDebugString() {
		StringBuffer sb = new StringBuffer(super.toDebugString()+"{");
		int i = 0;
		while (i < payload.length && i < maxDebugItems) {
			if (i>0) sb.append(",");
			sb.append(payload[i]);
			i++;
		}
		if (i < payload.length) sb.append(",..");
		return sb.toString()+"}";
	}	
}
