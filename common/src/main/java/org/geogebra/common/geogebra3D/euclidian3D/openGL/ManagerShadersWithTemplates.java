package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Manager that uses template geometries to avoid recalculate them
 * 
 * @author mathieu
 *
 */
abstract public class ManagerShadersWithTemplates extends ManagerShaders {

	/**
	 * number of templates for points
	 */
	final static public int POINT_TEMPLATES_COUNT = 3;

	private int[] pointGeometry;

	/**
	 * 
	 * @param pointSize
	 *            point size
	 * @return template index for this size
	 */
	static public int getIndexForPointSize(float pointSize) {
		return pointSize < 2.5f ? 0 : (pointSize > 5.5f ? 2 : 1);
	}

	/**
	 * 
	 * @param index
	 *            template index
	 * @return sphere size for template index
	 */
	static public int getSphereSizeForIndex(int index) {
		switch (index) {
		case 0:
			return 2;
		case 1:
			return 4;
		default:
			return 7;
		}
	}

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersWithTemplates(Renderer renderer,
			EuclidianView3D view3d) {
		super(renderer, view3d);

		// points geometry templates
		setScalerIdentity();
		pointGeometry = new int[3];
		for (int i = 0; i < 3; i++) {
			pointGeometry[i] = drawSphere(getSphereSizeForIndex(i), Coords.O,
					1d, -1);
		}
		setScalerView();

	}

	@Override
	public int drawPoint(DrawPoint3D d, float size, Coords center, int index) {
		scaleXYZ(center);
		return pointGeometry[getIndexForPointSize(size)];
	}

	@Override
	public void draw(int index, Coords center) {
		((RendererShadersInterface) renderer).setCenter(center);
		super.draw(index);
	}

}
