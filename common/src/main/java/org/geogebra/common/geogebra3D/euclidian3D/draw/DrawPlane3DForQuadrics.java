package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Helper for drawing planes, for quadrics
 * 
 * @author Proprietaire
 *
 */
public class DrawPlane3DForQuadrics extends DrawPlane3D {

	private GeoQuadric3D quadric;

	public DrawPlane3DForQuadrics(EuclidianView3D a_view3D, GeoPlane3D a_plane3D,
			GeoQuadric3D quadric) {
		super(a_view3D, a_plane3D, quadric);
	}

	@Override
	protected void init(GeoElement a_plane3D, GeoElement geo2) {

		super.init(a_plane3D);
		this.quadric = (GeoQuadric3D) geo2;

	}

	@Override
	public GeoElement getGeoElement() {
		return quadric;
	}

	@Override
	protected GeoPlane3D getPlane() {
		return (GeoPlane3D) super.getGeoElement();
	}

}
