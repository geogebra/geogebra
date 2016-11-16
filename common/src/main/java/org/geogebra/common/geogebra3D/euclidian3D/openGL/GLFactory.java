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
	private static volatile GLFactory prototype = null;

	private static final Object lock = new Object();

	public static GLFactory getPrototype() {
		return prototype;
	}

	public static void setPrototype(GLFactory p) {
		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	/**
	 * 
	 * @return new float buffer
	 */
	abstract public GLBuffer newBuffer();

	/**
	 * 
	 * @return new buffers stored in GPU
	 */
	abstract public GPUBuffer newGPUBuffer();

	/**
	 * 
	 * @return new short buffer for indices
	 */
	abstract public GLBufferIndices newBufferIndices();

}
