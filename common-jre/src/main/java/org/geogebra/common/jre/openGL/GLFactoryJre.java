package org.geogebra.common.jre.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

/**
 * GL factory for desktop
 *
 */
public class GLFactoryJre extends GLFactory {

	@Override
	final public GLBuffer newBuffer() {
		return new GLBufferJre();
	}

	@Override
	final public GPUBuffer newGPUBuffer() {
		return new GPUBufferJre();
	}

	@Override
	public GLBufferIndices newBufferIndices() {
		return new GLBufferIndicesJre();
	}
}
