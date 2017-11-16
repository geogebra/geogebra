package org.geogebra.common.jre.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

/**
 * Wrapper for GPU buffer
 */
public class GPUBufferJre implements GPUBuffer {

	private Integer impl;

	@Override
	public void set(Object index) {
		impl = (Integer) index;
	}

	@Override
	public Integer get() {
		return impl;
	}

}
