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
	 */
	public GLBufferD(ArrayList<Float> array){
		impl = FloatBuffer.allocate(array.size());
		for (int i = 0; i < array.size(); i++){
			impl.put(array.get(i));
		}
		impl.rewind();
	}

	public Float get() {
		return impl.get();
	}
	
	public void rewind(){
		impl.rewind();
	}
	
	public int capacity(){
		return impl.capacity();
	}

	public float[] array(){
		return impl.array();
	}
	
	
	
	
	/**
	 * 
	 * @return buffer
	 */
	public FloatBuffer getBuffer(){
		return impl;
	}
	
}
