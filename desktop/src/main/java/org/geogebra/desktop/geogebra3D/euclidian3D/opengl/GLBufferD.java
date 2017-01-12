package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

/**
 * buffers for openGL
 * 
 * @author Mathieu
 *
 */
public class GLBufferD implements GLBuffer {
	private FloatBuffer impl;

	/**
	 * constructor from float array
	 */
	public GLBufferD() {
		isEmpty = true;
	}

	private boolean isEmpty;

	private int currentLength;

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
			impl = FloatBuffer.allocate(length);
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
	public void put(double value) {
		impl.put((float) value);
	}

	@Override
	public double get() {
		return impl.get();
	}

	@Override
	public void rewind() {
		impl.rewind();
	}

	@Override
	public void set(ArrayList<Double> array, int length) {

		allocate(length);

		for (int i = 0; i < length; i++) {
			put(array.get(i));
		}

		setLimit(length);
	}

	@Override
	public int capacity() {
		return currentLength;
	}

	@Override
	public void array(float[] ret) {
		impl.rewind();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = impl.get();
		}
	}

	/**
	 * 
	 * @return buffer
	 */
	public FloatBuffer getBuffer() {
		return impl;
	}

}
