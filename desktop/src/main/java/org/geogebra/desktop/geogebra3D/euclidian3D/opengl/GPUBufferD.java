package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

public class GPUBufferD implements GPUBuffer {

	private Integer impl;

	public GPUBufferD() {
	}

	public void set(Object index) {
		impl = (Integer) index;
	}

	public Integer get() {
		return impl;
	}


}
