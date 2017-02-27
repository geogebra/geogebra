package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

public class GPUBufferD implements GPUBuffer {

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
