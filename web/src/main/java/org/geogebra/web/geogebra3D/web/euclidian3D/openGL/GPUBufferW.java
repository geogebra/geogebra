package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

import com.googlecode.gwtgl.binding.WebGLBuffer;

public class GPUBufferW implements GPUBuffer {

	private WebGLBuffer impl;

	public GPUBufferW() {
	}

	public void set(WebGLBuffer index) {
		impl = index;
	}

	public WebGLBuffer get() {
		return impl;
	}


}
