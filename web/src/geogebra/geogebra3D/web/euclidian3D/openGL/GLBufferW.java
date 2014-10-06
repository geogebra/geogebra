package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import java.util.ArrayList;

import com.googlecode.gwtgl.array.Float32Array;

/**
 * buffers for openGL
 * @author mathieu
 *
 */
public class GLBufferW implements GLBuffer {
	
	 private Float32Array impl;
	 
	
	/**
	 * constructor from float array
	 * @param array float array
	 */
	public GLBufferW(ArrayList<Float> array, int length){
		
		impl = Float32Array.create(length);
		for (int i = 0 ; i < length ; i++){
			impl.set(i, array.get(i));
		}
	}
	
	/**
	 * constructor from float array
	 */
	public GLBufferW(){
		isEmpty = true;
	}
	
	private boolean isEmpty;
	
	public boolean isEmpty(){
		return isEmpty;
	}

	public void setEmpty(){
		isEmpty = true;
	}
	
	public void set(ArrayList<Float> array, int length){
		
		// allocate buffer only at start and when length change
		if (impl == null || impl.getLength() != length){
			impl = Float32Array.create(length);
		}
		
		for (int i = 0 ; i < length ; i++){
			impl.set(i, array.get(i));
		}
		isEmpty = false;
	}


	public void rewind() {
	   // index = 0;
    }

	public int capacity() {
	    return impl.getLength();
    }

	public void array(float[] ret) {
		for (int i = 0 ; i < ret.length ; i++){
			ret[i] = impl.get(i);
		}
    }
	
	/**
	 * 
	 * @return buffer
	 */
	public Float32Array getBuffer(){
		return impl;
	}

}
