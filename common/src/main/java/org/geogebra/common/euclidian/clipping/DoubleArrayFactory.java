package org.geogebra.common.euclidian.clipping;

/**
 * Dummy implementation for DoubleArrayFactory only caring for known usage For
 * more professional implementation, the license should be minded
 */
public final class DoubleArrayFactory {

	private double[] double2 = new double[2];
	private double[] double6 = new double[6];

	private boolean d2free = true, d6free = true;

	/**
	 * Returns a double array of the indicated size.
	 * <P>
	 * If arrays of that size have previously been stored in this factory, then
	 * an existing array will be returned.
	 * 
	 * @param size
	 *            the array size you need.
	 * @return a double array of the size indicated.
	 */
	public double[] getArray(int size) {
		if (size == 2 && d2free) {
			d2free = false;
			return double2;
		} else if (size == 6 && d6free) {
			d6free = false;
			return double6;
		}
		return new double[size];
	}

	/**
	 * Stores an array for future use.
	 * <P>
	 * As soon as you call this method you should nullify all other references
	 * to the argument. If you continue to use it, and someone else retrieves
	 * this array by calling <code>getArray()</code>, then you may have two
	 * entities using the same array to manipulate data... and that can be
	 * really hard to debug!
	 * 
	 * @param array
	 *            the array you no longer need that might be needed later.
	 */
	public void putArray(double[] array) {
		if (array.length == 2)
			d2free = true;
		else if (array.length == 6)
			d6free = true;
	}
}
