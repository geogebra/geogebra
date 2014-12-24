package geogebra3D.euclidian3D.opengl;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * buffers for openGL
 * 
 * @author matthieu
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

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty() {
		isEmpty = true;
	}

	public void set(ArrayList<Float> array, int length) {

		// allocate buffer only at start and when length change
		if (impl == null || impl.capacity() < length) {
			impl = FloatBuffer.allocate(length);
		} else {
			impl.rewind();
		}

		impl.limit(length);

		for (int i = 0; i < length; i++) {
			impl.put(array.get(i));
		}
		impl.rewind();

		currentLength = length;
		isEmpty = false;
	}

	public int capacity() {
		return currentLength;
	}

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
