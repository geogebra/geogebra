package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;

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
