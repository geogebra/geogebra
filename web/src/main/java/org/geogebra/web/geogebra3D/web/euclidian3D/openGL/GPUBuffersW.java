package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffers;

import com.googlecode.gwtgl.binding.WebGLBuffer;

public class GPUBuffersW implements GPUBuffers {

	private WebGLBuffer[] impl;

	public GPUBuffersW() {
		impl = new WebGLBuffer[4];
	}

	public WebGLBuffer[] get() {
		return impl;
	}

	/**
	 * 
	 * @param attrib
	 *            attribute
	 * @return buffer for the attribute
	 */
	public WebGLBuffer get(int attrib) {
		return impl[attrib];
	}

}
