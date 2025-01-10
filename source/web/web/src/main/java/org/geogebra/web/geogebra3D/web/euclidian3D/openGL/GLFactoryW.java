package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;

/**
 * GL factory for web
 * 
 * @author mathieu
 *
 */
public class GLFactoryW extends GLFactory {

	@Override
	final public GLBuffer newBuffer() {
		return new GLBufferW();
	}

	@Override
	public GLBufferIndices newBufferIndices() {
		return new GLBufferIndicesW();
	}

}
