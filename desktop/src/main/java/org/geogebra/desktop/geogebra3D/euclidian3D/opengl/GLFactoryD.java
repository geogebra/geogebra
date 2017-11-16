package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.jre.openGL.GLBufferJre;
import org.geogebra.common.jre.openGL.GLBufferIndicesJre;

/**
 * GL factory for desktop
 * 
 * @author mathieu
 *
 */
public class GLFactoryD extends GLFactory {

	@Override
	final public GLBuffer newBuffer() {
		return new GLBufferJre();
	}

	@Override
	final public GPUBuffer newGPUBuffer() {
		return new GPUBufferD();
	}

	@Override
	public GLBufferIndices newBufferIndices() {
		return new GLBufferIndicesJre();
	}
}
