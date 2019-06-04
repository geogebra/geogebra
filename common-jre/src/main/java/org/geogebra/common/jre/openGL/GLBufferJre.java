package org.geogebra.common.jre.openGL;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

/**
 * buffers for openGL
 *
 */
public class GLBufferJre implements GLBuffer {
	private FloatBuffer impl;
	private boolean isEmpty;
	private int currentLength;

	/**
	 * constructor from float array
	 */
	public GLBufferJre() {
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
			impl = FloatBuffer.allocate(length);
		} else {
			((Buffer) impl).rewind();
		}

		((Buffer) impl).limit(length);

	}

	@Override
	public void setLimit(int length) {
		((Buffer) impl).limit(length);
		currentLength = length;

		((Buffer) impl).rewind();
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
		((Buffer) impl).rewind();
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
	public void set(ArrayList<Double> array, int offset, int length) {
		for (int i = 0; i < length; i++) {
			impl.put(i + offset, array.get(i).floatValue());
		}
	}

	@Override
	public void set(ArrayList<Double> array, int arrayOffset, int offset,
			int length) {
		for (int i = 0; i < length; i++) {
			impl.put(i + offset, array.get(arrayOffset + i).floatValue());
		}
	}

	@Override
	public void set(ArrayList<Double> array, float[] translate, float scale,
			int offset, int length) {
		for (int i = 0; i < length; i++) {
			impl.put(i + offset,
					array.get(i).floatValue() * scale + translate[i % 3]);
		}
	}

	@Override
	public void set(float value, int offset, int length, int step) {
		for (int i = 0; i < length; i++) {
			impl.put(i * step + offset, value);
		}
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

	@Override
	public void reallocate(int size) {
		FloatBuffer oldImpl = impl;
		impl = FloatBuffer.allocate(size);
		impl.put(oldImpl);
	}

	@Override
	public void position(int newPosition) {
		impl.rewind();
		impl.position(newPosition);
	}

}
