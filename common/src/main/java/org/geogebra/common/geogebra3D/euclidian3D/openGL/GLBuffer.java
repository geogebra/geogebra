package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

/**
 * interface for openGL buffers
 * 
 * @author mathieu
 *
 */
public interface GLBuffer {

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
	public void put(double value);

	/**
	 * 
	 * @return value at current buffer position
	 */
	public double get();

	/**
	 * rewind the buffer
	 */
	public void rewind();

	/**
	 * set this buffer with first values of array
	 * 
	 * @param array
	 *            values array
	 * @param length
	 *            length to copy
	 */
	public void set(ArrayList<Double> array, int length);

	/**
	 * @param array
	 *            values array
	 * @param offset
	 *            start offset
	 * @param length
	 *            length to copy
	 */
	public void set(ArrayList<Double> array, int offset, int length);

	/**
	 * @param array
	 *            values array
	 * @param arrayOffset
	 *            arrayOffset where to start in array
	 * @param offset
	 *            start offset
	 * @param length
	 *            length to copy
	 */
	public void set(ArrayList<Double> array, int arrayOffset, int offset,
			int length);

	/**
	 * @param array
	 *            values array
	 * @param translate
	 *            translation
	 * @param scale
	 *            scale
	 * @param offset
	 *            start offset
	 * @param length
	 *            length to copy
	 */
	public void set(ArrayList<Double> array, float[] translate, float scale,
			int offset, int length);

	/**
	 * set this values starting from offset, length times, with step between each
	 * indices
	 * 
	 * @param value
	 *            value
	 * @param offset
	 *            start offset
	 * @param length
	 *            length to copy
	 * @param step
	 *            step
	 */
	public void set(float value, int offset, int length, int step);

	/**
	 * 
	 * @return capacity
	 */
	public int capacity();

	/**
	 * set float array
	 * 
	 * @param ret
	 *            float array
	 */
	public void array(float[] ret);

	/**
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty();

	/**
	 * says it's an empty buffer
	 */
	public void setEmpty();

	/**
	 * reallocate to a bigger size and keep the values
	 * 
	 * @param size
	 *            new size
	 */
	public void reallocate(int size);

	/**
	 * set position where to read
	 * 
	 * @param newPosition
	 *            new position
	 */
	public void position(int newPosition);

}
