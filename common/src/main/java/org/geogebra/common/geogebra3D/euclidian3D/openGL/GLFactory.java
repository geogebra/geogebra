package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * factory for GL stuff
 * 
 * @author mathieu
 *
 */
public abstract class GLFactory {

	/**
	 * prototype to factor stuff
	 */
	public static GLFactory prototype = null;

	/**
	 * 
	 * @return new float buffer
	 */
	abstract public GLBuffer newBuffer();

	/**
	 * 
	 * @return new buffers stored in GPU
	 */
	abstract public GPUBuffers newGPUBuffers();
}
