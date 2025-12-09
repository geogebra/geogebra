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

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import elemental2.core.Float32Array;
import jsinterop.base.Js;

/**
 * buffers for openGL
 * 
 * @author mathieu
 *
 */
public class GLBufferW implements GLBuffer {

	private Float32Array impl;
	private boolean isEmpty;
	private int index = 0;

	private int currentLength;

	/**
	 * constructor from float array
	 */
	public GLBufferW() {
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
			impl = new Float32Array(length);
		}

		index = 0;
	}

	@Override
	public void setLimit(int length) {
		currentLength = length;
		isEmpty = false;
	}

	@Override
	public void put(double value) {
		if (impl == null) {
			return;
		}
		impl.setAt(index, value);
		index++;
	}

	@Override
	public double get() {
		double ret = impl.getAt(index);
		index++;
		return ret;
	}

	@Override
	public void rewind() {
		index = 0;
	}

	@Override
	public void set(ArrayList<Double> array, int length) {

		allocate(length);
		if (impl == null) {
			return;
		}
		for (int i = 0; i < length; i++) {
			impl.setAt(i, array.get(i));
		}

		setLimit(length);
	}

	@Override
	public void set(ArrayList<Double> array, int offset, int length) {
		for (int i = 0; i < length; i++) {
			impl.setAt(i + offset, array.get(i));
		}
	}

	@Override
	public void set(ArrayList<Double> array, int arrayOffset, int offset,
			int length) {
		for (int i = 0; i < length; i++) {
			impl.setAt(i + offset, array.get(arrayOffset + i));
		}
	}

	@Override
	public void set(ArrayList<Double> array, float[] translate, float scale,
			int offset, int length) {
		for (int i = 0; i < length; i++) {
			impl.setAt(i + offset,
					(double) (array.get(i).floatValue() * scale + translate[i % 3]));
		}
	}

	@Override
	public void set(float value, int offset, int length, int step) {
		for (int i = 0; i < length; i++) {
			impl.setAt(i * step + offset, (double) value);
		}
	}

	@Override
	public int capacity() {
		return currentLength;
	}

	@Override
	public void array(float[] ret) {
		if (impl == null) {
			return;
		}
		for (int i = 0; i < ret.length; i++) {
			ret[i] = impl.getAt(i).floatValue();
		}
	}

	/**
	 * 
	 * @return buffer
	 */
	public Float32Array getBuffer() {
		return Js.cast(impl.subarray(0, currentLength));
	}

	@Override
	public void reallocate(int size) {
		Float32Array oldImpl = impl;
		impl = new Float32Array(size);
		impl.set(oldImpl);
	}

	@Override
	public void position(int newPosition) {
		index = newPosition;
	}

}
