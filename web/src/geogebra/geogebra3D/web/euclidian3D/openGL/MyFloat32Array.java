package geogebra.geogebra3D.web.euclidian3D.openGL;

import com.googlecode.gwtgl.array.Float32Array;

/**
 * @author gabor
 *
 */
public class MyFloat32Array extends Float32Array {

	/**
	 * @param index
	 * @param value
	 */
	public final native void set(int index, double value) /*-{
		this[index] = value;
	}-*/;

}
