package geogebra3D.euclidian3D.opengl;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * buffers for openGL
 * @author matthieu
 *
 */
public class GLBufferD implements GLBuffer {
	private FloatBuffer impl;
	
	/**
	 * constructor from float array
	 * @param array float array
	 * @param length array length
	 */
	public GLBufferD(ArrayList<Float> array, int length){
		impl = FloatBuffer.allocate(length);
		for (int i = 0; i < length; i++){
			impl.put(array.get(i));
		}
		impl.rewind();
	}
	
	/**
	 * constructor from float array
	 */
	public GLBufferD(){
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
		if (impl == null || impl.capacity() != length){
			impl = FloatBuffer.allocate(length);
		}else{
			impl.rewind();
		}

		for (int i = 0; i < length; i++){
			impl.put(array.get(i));
		}
		impl.rewind();
		
		isEmpty = false;
	}

	
	public void rewind(){
		impl.rewind();
	}
	
	public int capacity(){
		return impl.capacity();
	}

	public void array(float[] ret){
		impl.rewind();
		for (int i = 0 ; i < ret.length ; i++){
			ret[i] = impl.get();
		}
	}
	
	
	
	
	/**
	 * 
	 * @return buffer
	 */
	public FloatBuffer getBuffer(){
		return impl;
	}
	
}
