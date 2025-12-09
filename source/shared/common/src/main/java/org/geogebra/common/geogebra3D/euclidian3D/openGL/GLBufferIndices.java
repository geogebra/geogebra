/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	 * put value at current buffer position
	 * 
	 * @param index
	 *            index
	 * @param value
	 *            value
	 */
	public void put(int index, short value);

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
	 * set float array
	 * 
	 * @param ret
	 *            float array
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
