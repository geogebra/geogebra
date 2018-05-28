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

	public void allocate(int length) {
		// not needed
	}

	public void setLimit(int length) {
		// not needed
	}

	public void put(short value) {
		addValue(value);
	}

	public void put(int index, short value) {
		// not needed
	}

	public short get() {
		// not needed
		return 0;
	}

	public void rewind() {
		// not needed
	}

	public int capacity() {
		// not needed
		return 0;
	}

	public void array(short[] ret) {
		// not needed
	}

	public void setEmpty() {
		// not needed
	}

	public void reallocate(int size) {
		// not needed
	}

	public void position(int newPosition) {
		// not needed
	}

}
