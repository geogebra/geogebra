package org.rosuda.REngine;

/** REXPDouble represents a vector of double precision floating point values. */
public class REXPDouble extends REXPVector {
	private double[] payload;
	
	/** NA real value as defined in R. Note: it can NOT be used in comparisons, you must use {@link #isNA(double)} instead. */
	public static final double NA = Double.longBitsToDouble(0x7ff00000000007a2L);
	
	/** Java screws up the bits in NA real values, so we cannot compare to the real bits used by R (0x7ff00000000007a2L) but use this value which is obtained by passing the bits through Java's double type */
	static final long NA_bits = Double.doubleToRawLongBits(Double.longBitsToDouble(0x7ff00000000007a2L));
	
	/** checks whether a given double value is a NA representation in R. Note that NA is NaN but not all NaNs are NA. */
	public static boolean isNA(double value) {
		/* on OS X i386 the MSB of the fraction is set even though R doesn't set it.
		   Although this is technically a good idea (to make it a QNaN) it's not what R does and thus makes the comparison tricky */
		return (Double.doubleToRawLongBits(value) & 0xfff7ffffffffffffL) == (NA_bits & 0xfff7ffffffffffffL);
	}

	/** create real vector of the length 1 with the given value as its first (and only) element */
	public REXPDouble(double load) {
		super();
		payload=new double[] { load };
	}
	
	public REXPDouble(double[] load) {
		super();
		payload=(load==null)?new double[0]:load;
	}

	public REXPDouble(double[] load, REXPList attr) {
		super(attr);
		payload=(load==null)?new double[0]:load;
	}
	
	public int length() { return payload.length; }

	public Object asNativeJavaObject() {
		return payload;
	}

	/** return <code>true</code> */
	public boolean isNumeric() { return true; }

	/** returns the values represented by this vector */
	public double[] asDoubles() { return payload; }

	/** converts the values of this vector into integers by cast */
	public int[] asIntegers() {
		int[] a = new int[payload.length];
		int i = 0;
		while (i < payload.length) { a[i] = (int) payload[i]; i++; }
		return a;
	}

	/** converts the values of this vector into strings */
	public String[] asStrings() {
		String[] s = new String[payload.length];
		int i = 0;
		while (i < payload.length) { s[i] = ""+payload[i]; i++; }
		return s;
	}
	
	/** returns a boolean vector of the same length as this vector with <code>true</code> for NA values and <code>false</code> for any other values (including NaNs) */
	public boolean[] isNA() {
		boolean a[] = new boolean[payload.length];
		int i = 0;
		while (i < a.length) { a[i] = isNA(payload[i]); i++; }
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
