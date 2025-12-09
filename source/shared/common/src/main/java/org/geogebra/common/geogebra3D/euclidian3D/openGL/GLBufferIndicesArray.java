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
 * ArrayList that implements GLBufferIndices to be able to get indices values
 * (see points templates)
 *
 */
public class GLBufferIndicesArray extends ReusableArrayList<Short>
		implements GLBufferIndices {

	public GLBufferIndicesArray(int size) {
		super(size);
	}

	@Override
	public void allocate(int length) {
		// not needed
	}

	@Override
	public void setLimit(int length) {
		// not needed
	}

	@Override
	public void put(short value) {
		addValue(value);
	}

	@Override
	public void put(int index, short value) {
		// not needed
	}

	@Override
	public short get() {
		// not needed
		return 0;
	}

	@Override
	public void rewind() {
		// not needed
	}

	@Override
	public int capacity() {
		// not needed
		return 0;
	}

	@Override
	public void array(short[] ret) {
		// not needed
	}

	@Override
	public void setEmpty() {
		// not needed
	}

	@Override
	public void reallocate(int size) {
		// not needed
	}

	@Override
	public void position(int newPosition) {
		// not needed
	}

}
