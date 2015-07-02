package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.nio.ShortBuffer;
import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;

/**
 * buffers for openGL
 * 
 * @author matthieu
 *
 */
public class GLBufferIndicesD implements GLBufferIndices {
	private ShortBuffer impl;

	/**
	 * constructor from float array
	 */
	public GLBufferIndicesD() {
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
			impl = ShortBuffer.allocate(length);
		} else {
			impl.rewind();
		}
		
		impl.limit(length);
		
	}
	
	public void setLimit(int length){
		impl.limit(length);
		currentLength = length;
		
		impl.rewind();
		isEmpty = false;
	}
	
	public void put(short value) {
		impl.put(value);
	}

	public short get() {
		return impl.get();
	}

	public void rewind() {
		impl.rewind();
	}

	public void set(ArrayList<Short> array, int length) {

		allocate(length);
		
		for (int i = 0; i < length; i++) {
			put(array.get(i));
		}
		
		setLimit(length);
	}

	public int capacity() {
		return currentLength;
	}

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

}
