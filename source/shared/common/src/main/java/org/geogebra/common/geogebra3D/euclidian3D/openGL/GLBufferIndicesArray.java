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
