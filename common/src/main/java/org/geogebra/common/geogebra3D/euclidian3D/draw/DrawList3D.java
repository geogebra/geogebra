package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public class DrawList3D extends DrawComposite3D {

	private GeoList geoList;

	/**
	 * common constructor
	 * 
	 * @param view3D
	 *            view
	 * @param geo
	 *            list
	 */
	public DrawList3D(EuclidianView3D view3D, GeoList geo) {
		super(view3D, geo);
		this.geoList = geo;

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	protected int size() {
		return geoList.size();
	}

	@Override
	protected GeoElement getElement(int i) {
		return geoList.get(i);
	}

	@Override
	public boolean isVisible() {
		if (getGeoElement().isLabelSet() && createdByDrawList()) {
			return false;
		}
		return super.isVisible();
	}
}
