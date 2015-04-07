package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import com.googlecode.gwtgl.array.Float32Array;

/**
 * @author gabor
 *
 */
public class MyFloat32Array extends Float32Array {

	protected MyFloat32Array() {
		// put this because of a GWT compile error...
		super();
	}
	/**
	 * @param index
	 * @param value
	 */
	public final native void set(int index, double value) /*-{
		this[index] = value;
	}-*/;

}
