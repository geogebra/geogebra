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

	/**
	 * @return factory singleton instance
	 */
	public static GLFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            factory prototype
	 */
	public static void setPrototypeIfNull(GLFactory p) {
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
	 * @return new short buffer for indices
	 */
	abstract public GLBufferIndices newBufferIndices();

}
