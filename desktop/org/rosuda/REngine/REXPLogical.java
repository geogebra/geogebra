package org.rosuda.REngine;

/** REXPLogical represents a vector of logical values (TRUE, FALSE or NA). Unlike Java's boolean type R's logicals support NA values therefore either of {@link #isTRUE()}, {@link #isFALSE()} or {@link #isNA()} must be used to convert logicals to boolean values. */
public class REXPLogical extends REXPVector {
	protected byte[] payload;
	
	/** NA integer value as defined in R. Unlike its real equivalent this one can be used in comparisons, although {@link #isNA(int) } is provided for consistency. */
	static final int NA_internal = -2147483648;

	/** NA boolean value as used in REXPLogical implementation. This differs from the value used in R since R uses int data type and we use byte. Unlike its real equivalent this one can be used in comparisons, although {@link #isNA(byte) } is provided for consistency. */
	public static final byte NA = -128;
	public static final byte TRUE = 1;
	public static final byte FALSE = 0;
	
	public static boolean isNA(byte value) {
		return (value == NA);
	}
	
	/** create logical vector of the length 1 with the given value as its first (and only) element */
	public REXPLogical(boolean load) {
		super();
		payload = new byte[] { load ? TRUE : FALSE };
	}

	/** create logical vector of the length 1 with the given value as its first (and only) element */
	public REXPLogical(byte load) {
		super();
		payload = new byte[] { load };
	}
	
	/** create logical vector with the payload specified by <code>load</code> */
	public REXPLogical(byte[] load) {
		super();
		payload = (load==null) ? new byte[0]:load;
	}

	/** create logical vector with the payload specified by <code>load</code> */
	public REXPLogical(boolean[] load) {
		super();
		payload = new byte[(load == null) ? 0 : load.length];
		if (load != null)
			for (int i = 0; i < load.length; i++)
				payload[i] = load[i] ? TRUE : FALSE;
	}
	
	/** create integer vector with the payload specified by <code>load</code> and attributes <code>attr</code> */
	public REXPLogical(byte[] load, REXPList attr) {
		super(attr);
		payload = (load==null) ? new byte[0] : load;
	}
	
	/** create integer vector with the payload specified by <code>load</code> and attributes <code>attr</code> */
	public REXPLogical(boolean[] load, REXPList attr) {
		super(attr);
		payload = new byte[(load == null) ? 0 : load.length];
		if (load != null)
			for (int i = 0; i < load.length; i++)
				payload[i] = load[i] ? TRUE : FALSE;
	}

	public int length() { return payload.length; }

	public boolean isLogical() { return true; }

	public Object asNativeJavaObject() {
		return payload;
	}

	public int[] asIntegers() {
		int p[] = new int[payload.length];
		for (int i = 0; i < payload.length; i++) // map bytes to integers including NA representation
			p[i] = (payload[i] == NA) ? REXPInteger.NA : ((payload[i] == FALSE) ? 0 : 1);
		return p;
	}

	public byte[] asBytes() { return payload; }

	/** returns the contents of this vector as doubles */
	public double[] asDoubles() {
		double[] d = new double[payload.length];
		for (int i = 0; i < payload.length; i++)
			d[i] = (payload[i] == NA) ? REXPDouble.NA : ((payload[i] == FALSE) ? 0.0 : 1.0);
		return d;
	}

	/** returns the contents of this vector as strings */
	public String[] asStrings() {
		String[] s = new String[payload.length];
		for (int i = 0; i < payload.length; i++)
			s[i] = (payload[i] == NA) ? "NA" : ((payload[i] == FALSE) ? "FALSE" : "TRUE");
		return s;
	}
	
	public boolean[] isNA() {
		boolean a[] = new boolean[payload.length];
		int i = 0;
		while (i < a.length) { a[i] = (payload[i] == NA); i++; }
		return a;
	}
	
	/** returns a boolean array of the same langth as the receiver with <code>true</code> for <code>TRUE</code> values and <code>false</code> for <code>FALSE</code> and <code>NA</code> values.
	 @return boolean array */
	public boolean[] isTRUE() {
		boolean a[] = new boolean[payload.length];
		int i = 0;
		while (i < a.length) { a[i] = (payload[i] != NA && payload[i] != FALSE); i++; }
		return a;
	}
	
	/** returns a boolean array of the same langth as the receiver with <code>true</code> for <code>FALSE</code> values and <code>false</code> for <code>TRUE</code> and <code>NA</code> values.
	 @return boolean array */
	public boolean[] isFALSE() {
		boolean a[] = new boolean[payload.length];
		int i = 0;
		while (i < a.length) { a[i] = (payload[i] == FALSE); i++; }
		return a;
	}
	
	/** returns a boolean array of the same langth as the receiver with <code>true</code> for <code>TRUE</code> values and <code>false</code> for <code>FALSE</code> and <code>NA</code> values.
	 @return boolean array
	 @deprecated replaced by {@link #isTRUE()} for consistency with R nomenclature. */
	public boolean[] isTrue() { return isTRUE(); }

	/** returns a boolean array of the same langth as the receiver with <code>true</code> for <code>FALSE</code> values and <code>false</code> for <code>TRUE</code> and <code>NA</code> values.
	 @return boolean array
	 @deprecated replaced by {@link #isTRUE()} for consistency with R nomenclature. */
	public boolean[] isFalse() { return isFALSE(); }

	public String toDebugString() {
		StringBuffer sb = new StringBuffer(super.toDebugString()+"{");
		int i = 0;
		while (i < payload.length && i < maxDebugItems) {
			if (i>0) sb.append(",");
			sb.append((payload[i] == NA) ? "NA" : ((payload[i] == FALSE) ? "FALSE" : "TRUE"));
			i++;
		}
		if (i < payload.length) sb.append(",..");
		return sb.toString()+"}";
	}
}
