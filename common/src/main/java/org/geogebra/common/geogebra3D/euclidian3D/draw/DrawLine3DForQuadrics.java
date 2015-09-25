package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Draw line for quadrics
 * 
 * @author mathieu
 *
 */
public class DrawLine3DForQuadrics extends DrawLine3D {

	public DrawLine3DForQuadrics(EuclidianView3D a_view3D, GeoLine3D line,
			GeoQuadric3D quadric) {
		super(a_view3D, line, quadric);
	}

	private GeoQuadric3D quadric;

	protected void init(GeoElement line, GeoElement geo2) {

		super.init(line);
		this.quadric = (GeoQuadric3D) geo2;

	}

	@Override
	public GeoElement getGeoElement() {
		return quadric;
	}

	@Override
	protected GeoLine3D getLine() {
		return (GeoLine3D) super.getGeoElement();
	}
}
