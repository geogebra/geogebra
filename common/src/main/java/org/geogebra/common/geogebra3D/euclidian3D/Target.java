package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

/**
 * Class for drawing a target when placing a point (in AR)
 *
 */
public class Target {

	private TargetType type;

	/**
	 * Constructor
	 */
	public Target() {
		type = TargetType.NOT_USED;
	}

	/**
	 * 
	 * @return target type
	 */
	public TargetType getType() {
		return type;
	}

	/**
	 * update type for view
	 * 
	 * @param view
	 *            3D view
	 */
	public void updateType(EuclidianView3D view) {
		type = TargetType.getCurrentTargetType(view,
				(EuclidianController3D) view.getEuclidianController());
	}

	/**
	 * draw target
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param view
	 *            3D view
	 */
	public void draw(Renderer renderer, EuclidianView3D view) {
		type.drawTarget(renderer, view);
	}

}
