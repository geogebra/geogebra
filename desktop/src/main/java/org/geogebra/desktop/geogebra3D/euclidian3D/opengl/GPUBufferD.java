package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

public class GPUBufferD implements GPUBuffer {

	private int impl;

	public GPUBufferD() {
	}

	public void set(int index) {
		impl = index;
	}

	public int get() {
		return impl;
	}


}
