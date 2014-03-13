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
	 
	 private float[] values; 
	 private int index;
	
	/**
	 * constructor from float array
	 * @param array float array
	 */
	public GLBufferW(ArrayList<Float> array){
		
		values = new float[array.size()];
		index = 0;
		for (float v : array){
			values[index] = v;
			index++;
		}
		impl = Float32Array.create(values);
		index = 0;
	}

	public Float get() {
	    Float ret = values[index];
	    index++;
	    return ret;
    }

	public void rewind() {
	    index = 0;
    }

	public int capacity() {
	    return impl.getLength();
    }

	public float[] array() {
	    return values;
    }
	
	/**
	 * 
	 * @return buffer
	 */
	public Float32Array getBuffer(){
		return impl;
	}

}
