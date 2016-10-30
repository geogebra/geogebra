package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * interface for indices buffers
 * 
 * @author mathieu
 *
 */
public interface GLBufferIndices {

	/**
	 * allocate memory if needed
	 * 
	 * @param length
	 *            length
	 */
	public void allocate(int length);

	/**
	 * set limit to which we use the buffer
	 * 
	 * @param length
	 *            limit
	 */
	public void setLimit(int length);

	/**
	 * put value at current buffer position
	 * 
	 * @param value
	 *            value
	 */
	public void put(short value);

	/**
	 * 
	 * @return value at current buffer position
	 */
	public short get();

	/**
	 * rewind the buffer
	 */
	public void rewind();

	/**
	 * 
	 * @return capacity
	 */
	public int capacity();

	/**
	 * 
	 * set float array
	 */
	public void array(short[] ret);

	/**
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty();

	/**
	 * says it's an empty buffer
	 */
	public void setEmpty();
}
