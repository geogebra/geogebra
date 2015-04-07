package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;

/**
 * GL factory for web
 * 
 * @author mathieu
 *
 */
public class GLFactoryW extends GLFactory {

	/**
	 * constructor
	 */
	public GLFactoryW() {

	}

	@Override
	final public GLBuffer newBuffer() {
		return new GLBufferW();
	}
}
