package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import java.util.ArrayList;

import com.googlecode.gwtgl.array.Float32Array;

/**
 * buffers for openGL
 * @author matthieu
 *
 */
public class GLBufferW implements GLBuffer {
	
	 private Float32Array impl;
	
	/**
	 * constructor from float array
	 * @param array float array
	 */
	public GLBufferW(ArrayList<Float> array){
		
		float[] values = new float[array.size()];
		int i = 0;
		for (float v : array){
			values[i] = v;
			i++;
		}
		impl = Float32Array.create(values);
	}

	public Float get() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void rewind() {
	    // TODO Auto-generated method stub
	    
    }

	public int capacity() {
	    // TODO Auto-generated method stub
	    return impl.getLength();
    }

	public float[] array() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	/**
	 * 
	 * @return buffer
	 */
	public Float32Array getBuffer(){
		return impl;
	}

}
