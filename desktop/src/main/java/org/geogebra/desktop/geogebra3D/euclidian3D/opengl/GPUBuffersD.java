package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffers;

public class GPUBuffersD implements GPUBuffers {

	private int[] impl;

	public GPUBuffersD() {
		impl = new int[5];
	}

	public int[] get() {
		return impl;
	}

	/**
	 * 
	 * @param attrib
	 *            attribute
	 * @return buffer for the attribute
	 */
	public int get(int attrib) {
		return impl[attrib];
	}

}
