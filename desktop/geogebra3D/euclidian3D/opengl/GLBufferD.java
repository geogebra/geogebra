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
	
	public void allocate(int length){

		// allocate buffer only at start and when length change
		if (impl == null || impl.capacity() < length) {
			impl = FloatBuffer.allocate(length);
		} else {
			impl.rewind();
		}
		
	}
	
	public void setLimit(int length){
		impl.limit(length);
		currentLength = length;
	}
	
	public void put(float value){
		impl.put(value);
	}

	public void endOfPut(){
		impl.rewind();
		isEmpty = false;
	}

	public void set(ArrayList<Float> array, int length) {

		allocate(length);
		setLimit(length);
		
		for (int i = 0; i < length; i++) {
			put(array.get(i));
		}
		
		endOfPut();
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
