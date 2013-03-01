package geogebra.common.euclidian.clipping;

/**
 * Factory for arrays of doubles (uses pooling to make instantiation faster)
 */
public abstract class DoubleArrayFactory {

	/**
	 * Platform dependent instance of this factory
	 */
	public static DoubleArrayFactory prototype = null;
	/** Mutable integer key */
	protected MutableInteger key = new MutableInteger(0);
	
	/** Returns a double array of the indicated size.
	 * <P>If arrays of that size have previously been
	 * stored in this factory, then an existing array
	 * will be returned.
	 * @param size the array size you need.
	 * @return a double array of the size indicated.
	 */
	public abstract double[] getArray(int size);
	
	/** Stores an array for future use.
	 * <P>As soon as you call this method you should nullify
	 * all other references to the argument.  If you continue
	 * to use it, and someone else retrieves this array
	 * by calling <code>getArray()</code>, then you may have
	 * two entities using the same array to manipulate data...
	 * and that can be really hard to debug!
	 * 
	 * @param array the array you no longer need that might be
	 * needed later.
	 */
	public abstract void putArray(double[] array);
	
	
}
