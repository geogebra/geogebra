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

package org.geogebra.common.jre.openGL;

import java.nio.ShortBuffer;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;

/**
 * buffers for openGL
 * 
 * @author Mathieu
 *
 */
public class GLBufferIndicesJre implements GLBufferIndices {
	private ShortBuffer impl;
	private boolean isEmpty;

	private int currentLength;

	/**
	 * constructor from float array
	 */
	public GLBufferIndicesJre() {
		isEmpty = true;
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
		if (impl == null || impl.capacity() < length) {
			impl = ShortBuffer.allocate(length);
		} else {
			impl.rewind();
		}

		impl.limit(length);

	}

	@Override
	public void setLimit(int length) {
		impl.limit(length);
		currentLength = length;

		impl.rewind();
		isEmpty = false;
	}

	@Override
	public void put(short value) {
		impl.put(value);
	}

	@Override
	public void put(int index, short value) {
		impl.put(index, value);
	}

	@Override
	public short get() {
		return impl.get();
	}

	@Override
	public void rewind() {
		impl.rewind();
	}

	@Override
	public int capacity() {
		return currentLength;
	}

	@Override
	public void array(short[] ret) {
		impl.rewind();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = impl.get();
		}
	}

	/**
	 * 
	 * @return buffer
	 */
	public ShortBuffer getBuffer() {
		return impl;
	}

	@Override
	public void reallocate(int size) {
		ShortBuffer oldImpl = impl;
		impl = ShortBuffer.allocate(size);
		impl.put(oldImpl);
	}

	@Override
	public void position(int newPosition) {
		impl.rewind();
		impl.position(newPosition);
	}

}
