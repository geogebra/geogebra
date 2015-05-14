package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;

/**
 * Class for drawing 3D constant planes.
 * 
 * @author matthieu
 *
 */
public class DrawPlaneConstant3D extends DrawPlane3D {

	private DrawAxis3D xAxis, yAxis;

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param a_plane3D
	 *            plane
	 * @param xAxis
	 *            x axis
	 * @param yAxis
	 *            y axis
	 */
	public DrawPlaneConstant3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D,
			DrawAxis3D xAxis, DrawAxis3D yAxis) {

		super(a_view3D, a_plane3D);

		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

	@Override
	protected boolean updateForItSelf() {

		double[] xMinMax = xAxis.getDrawMinMax();
		double[] yMinMax = yAxis.getDrawMinMax();

		GeoPlane3D geo = (GeoPlane3D) getGeoElement();

		geo.setGridCorners(xMinMax[0], yMinMax[0], xMinMax[1], yMinMax[1]);

		geo.setGridDistances(getView3D().getGridDistances(0), getView3D()
				.getGridDistances(1));

		if (getView3D().getShowPlane()) {
			super.updateGeometry();
		}

		return true;
	}

	@Override
	protected void updateForView() {
		// follow the view
	}

	@Override
	protected void setMinMax() {
		// follow axes values
	}

	@Override
	protected boolean isGridVisible() {
		return ((GeoPlane3D) getGeoElement()).isGridVisible();
	}

}
