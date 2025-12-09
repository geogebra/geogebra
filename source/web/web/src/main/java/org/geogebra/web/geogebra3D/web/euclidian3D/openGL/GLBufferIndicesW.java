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

package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;

import elemental2.core.Int16Array;

/**
 * buffers for openGL
 * 
 * @author mathieu
 *
 */
public class GLBufferIndicesW implements GLBufferIndices {

	private Int16Array impl;
	private boolean isEmpty;
	private int currentLength;
	private int mIndex = 0;

	/**
	 * constructor from float array
	 */
	public GLBufferIndicesW() {
		isEmpty = true;
		currentLength = 0;
	}

	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	@Override
	public void setEmpty() {
		isEmpty = true;
	}

	@Override
	public void allocate(int length) {
		// allocate buffer only at start and when length change
		if (impl == null || impl.getLength() < length) {
			impl = new Int16Array(length);
		}

		mIndex = 0;

	}

	@Override
	public void setLimit(int length) {
		currentLength = length;
		isEmpty = false;
	}

	@Override
	public void put(short value) {
		if (impl == null) {
			return;
		}
		impl.setAt(mIndex, (double) value);
		mIndex++;
	}

	@Override
	public void put(int index, short value) {
		if (impl == null) {
			return;
		}
		impl.setAt(index, (double) value);
	}

	@Override
	public short get() {
		short ret = impl.getAt(mIndex).shortValue();
		mIndex++;
		return ret;
	}

	@Override
	public void rewind() {
		mIndex = 0;
	}

	@Override
	public int capacity() {
		return currentLength;
	}

	@Override
	public void array(short[] ret) {
		if (impl == null) {
			return;
		}
		for (int i = 0; i < ret.length; i++) {
			ret[i] = impl.getAt(i).shortValue();
		}
	}

	/**
	 * 
	 * @return buffer
	 */
	public Int16Array getBuffer() {
		return impl;
	}

	@Override
	public void reallocate(int size) {
		Int16Array oldImpl = impl;
		impl = new Int16Array(size);
		impl.set(oldImpl);
	}

	@Override
	public void position(int newPosition) {
		mIndex = newPosition;
	}

}
