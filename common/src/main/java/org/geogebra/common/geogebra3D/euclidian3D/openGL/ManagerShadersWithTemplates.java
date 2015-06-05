package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Manager that uses template geometries to avoid recalculate them
 * 
 * @author mathieu
 *
 */
public class ManagerShadersWithTemplates extends ManagerShaders {

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersWithTemplates(Renderer renderer, EuclidianView3D view3d) {
		super(renderer, view3d);

		// points geometry templates
		pointGeometry = new int[3];
		pointGeometry[0] = drawSphere(2, Coords.O, 1d, -1);
		pointGeometry[1] = drawSphere(4, Coords.O, 1d, -1);
		pointGeometry[2] = drawSphere(7, Coords.O, 1d, -1);

	}

	private int[] pointGeometry;

	@Override
	public int drawPoint(int size, Coords center, int index) {

		// find point geometry template
		int i = 1;
		// int size2 = 4;
		if (size < 3) {
			i = 0;
			// size2 = 2;
		} else if (size > 5) {
			i = 2;
			// size2 = 7;
		}
		// if (pointGeometry[i] == -1){
		// pointGeometry[i] = drawSphere(size2, Coords.O, 1d);
		// }

		return pointGeometry[i];
	}

	@Override
	public void draw(int index, Coords center) {
		((RendererShadersInterface) renderer).setCenter(center);
		super.draw(index);
	}


}
