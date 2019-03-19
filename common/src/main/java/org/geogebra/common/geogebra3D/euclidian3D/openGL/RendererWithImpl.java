package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

/**
 * Renderer using "implementation" for java/web/android/etc.
 * 
 * @author mathieu
 *
 */
public abstract class RendererWithImpl extends Renderer {

	/**
	 * basic constructor
	 * 
	 * @param view
	 *            3D view
	 * @param type
	 *            GL2/SHADER
	 */
	public RendererWithImpl(EuclidianView3D view, RendererType type) {
		super(view, type);
	}

	/**
	 * dummy renderer (when no GL available)
	 */
	public RendererWithImpl() {
		super();
	}

}
